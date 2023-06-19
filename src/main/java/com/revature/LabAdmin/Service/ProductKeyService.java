package com.revature.LabAdmin.Service;

import com.revature.LabAdmin.Exception.KeyException;
import com.revature.LabAdmin.Exception.UnauthorizedException;
import com.revature.LabAdmin.Model.ProductKey;
import com.revature.LabAdmin.Repository.ProductKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductKeyService {
    /**
     * create a new user product key
     */

    ProductKeyRepository productKeyRepository;
    AuthService authService;
    @Autowired
    public ProductKeyService(ProductKeyRepository productKeyRepository, AuthService authService){
        this.productKeyRepository = productKeyRepository;
    }

    /**
     * create a new key for admins/users
     * @param key
     * @param newProductKey
     * @return
     * @throws UnauthorizedException
     */
    public ProductKey attemptKeyGeneration(long key, ProductKey newProductKey) throws UnauthorizedException {
        if(newProductKey.isSuperAdmin()){
            return null;
        }if(newProductKey.isAdmin()){
            if(authService.validateSuperAdmin(key)){
                newProductKey.setBatchId(0);
                newProductKey.setActive(true);
                newProductKey.setProductKey((long) (Math.random()*Long.MAX_VALUE));
                newProductKey = productKeyRepository.save(newProductKey);
                return newProductKey;
            }else{
                throw new UnauthorizedException();
            }
        }else{
            if(authService.validateAdmin(key)){
                newProductKey.setActive(true);
                newProductKey.setProductKey((long) (Math.random()*Long.MAX_VALUE));
                newProductKey = productKeyRepository.save(newProductKey);
                return newProductKey;
            }else{
                throw new UnauthorizedException();
            }
        }

    }

    /**
     * create a new superadmin - for testing
     */
    public ProductKey superAdminKeyGeneration(long key) throws UnauthorizedException {
        ProductKey newKey = new ProductKey( (key), true, true, true);
        newKey = productKeyRepository.save(newKey);
        return newKey;
    }
    /**
     * invalidate a user key
     */
    public ProductKey attemptKeyInvalidation(long key, long target) throws UnauthorizedException, KeyException {
        Optional<ProductKey> invalidKeyOptional = productKeyRepository.findById(target);
        if(!invalidKeyOptional.isPresent()){
            throw new KeyException();
        }else{
            ProductKey invalidKey = invalidKeyOptional.get();
            if(invalidKey.isSuperAdmin()){
                throw new UnauthorizedException();
            }else if(invalidKey.isAdmin()){
                if(authService.validateSuperAdmin(key)){
                    ProductKey oldKey = productKeyRepository.findById(invalidKey.getProductKey()).get();
                    oldKey.setActive(false);
                    productKeyRepository.save(oldKey);
                    return oldKey;
                }else{
                    throw new UnauthorizedException();
                }
            }else{
                if(authService.validateAdmin(key)){
                    ProductKey oldKey = productKeyRepository.findById(invalidKey.getProductKey()).get();
                    oldKey.setActive(false);
                    productKeyRepository.save(oldKey);
                    return oldKey;
                }else{
                    throw new UnauthorizedException();
                }
            }
        }
    }
}
