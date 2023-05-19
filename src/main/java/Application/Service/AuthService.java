package Application.Service;

import Application.Model.ProductKey;
import Application.Repository.ProductKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    ProductKeyRepository productKeyRepository;

    public boolean validateUser(long pkey){

        Optional<ProductKey> productKey = productKeyRepository.findById(pkey);
        if(productKey.isPresent()){
            return productKey.get().isActive();
        }
        return false;
    }
    public boolean validateAdmin(long pkey){
        ProductKey productKey = productKeyRepository.findById(pkey).get();
        return productKey.isAdmin() && productKey.isActive();
    }
    public boolean validateSuperAdmin(long pkey){
        ProductKey productKey = productKeyRepository.findById(pkey).get();
        return productKey.isSuperAdmin() && productKey.isActive();
    }
    public ProductKey getProductKey(long pkey){
        return productKeyRepository.findById(pkey).get();
    }
}
