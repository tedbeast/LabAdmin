package Application.Service;

import Application.Model.ProductKey;
import Application.Repository.ProductKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    ProductKeyRepository productKeyRepository;

    public boolean validateUser(long pkey){
        ProductKey productKey = productKeyRepository.findById(pkey).get();
        return productKey.isActive();
    }
    public boolean validateAdmin(long pkey){
        ProductKey productKey = productKeyRepository.findById(pkey).get();
        return productKey.isAdmin() && productKey.isActive();
    }
    public boolean validateSuperAdmin(long pkey){
        ProductKey productKey = productKeyRepository.findById(pkey).get();
        return productKey.isSuperAdmin() && productKey.isActive();
    }
}
