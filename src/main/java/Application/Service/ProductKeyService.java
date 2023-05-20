package Application.Service;

import Application.Exception.UnauthorizedException;
import Application.Model.ProductKey;
import Application.Repository.ProductKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductKeyService {
    /**
     * create a new user product key
     */
    @Autowired
    ProductKeyRepository productKeyRepository;
    public ProductKey attemptUserKeyGeneration(long key) throws UnauthorizedException {
        ProductKey productKey = productKeyRepository.findById(key).get();
        if(productKey.admin == true){
            ProductKey newKey = new ProductKey((long) (Math.random()*Long.MAX_VALUE), true, false, false);
            newKey = productKeyRepository.save(newKey);
            return newKey;
        }else{
            throw new UnauthorizedException();
        }
    }

    /**
     * create a new admin key
     */
    public ProductKey attemptAdminKeyGeneration(long key) throws UnauthorizedException {
        ProductKey productKey = productKeyRepository.findById(key).get();
        if(productKey.superAdmin == true){
            ProductKey newKey = new ProductKey((long) (Math.random()*Long.MAX_VALUE), true, true, false);
            newKey = productKeyRepository.save(newKey);
            return newKey;
        }else{
            throw new UnauthorizedException();
        }
    }

    /**
     * create a new superadmin
     */
    public ProductKey superAdminKeyGeneration(long key) throws UnauthorizedException {
        ProductKey newKey = new ProductKey( (key), true, true, true);
        newKey = productKeyRepository.save(newKey);
        return newKey;
    }

    /**
     * invalidate a user key
     */
    public ProductKey attemptInvalidateUserKey(long key, ProductKey invalidKey) throws UnauthorizedException {
        ProductKey productKey = productKeyRepository.findById(key).get();
        if(productKey.admin == true){
            ProductKey oldKey = productKeyRepository.findById(invalidKey.getProductKey()).get();
            oldKey.setActive(false);
            productKeyRepository.save(oldKey);
            return oldKey;
        }else{
            throw new UnauthorizedException();
        }
    }

    /**
     * invalidate an admin key
     */
    public ProductKey attemptInvalidateAdminKey(long key, ProductKey invalidKey) throws UnauthorizedException {
        ProductKey productKey = productKeyRepository.findById(key).get();
        if(productKey.superAdmin == true){
            ProductKey oldKey = productKeyRepository.findById(invalidKey.getProductKey()).get();
            oldKey.setActive(false);
            productKeyRepository.save(oldKey);
            return oldKey;
        }else{
            throw new UnauthorizedException();
        }
    }
    public ProductKey getProductKey(long pkey){
        return productKeyRepository.findById(pkey).get();
    }

}
