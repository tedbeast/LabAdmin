package Application.Controller;

import Application.Exception.UnauthorizedException;
import Application.Model.ProductKey;
import Application.Service.ProductKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

public class AdminController {
    @Autowired
    public ProductKeyService productKeyService;
    @GetMapping
    public ProductKey genNewUserKey(@RequestHeader long product_key) throws UnauthorizedException {
        return productKeyService.attemptUserKeyGeneration(product_key);
    }
    @GetMapping
    public ProductKey genNewAdminKey(@RequestHeader long product_key) throws UnauthorizedException {
        return productKeyService.attemptAdminKeyGeneration(product_key);
    }
    @PatchMapping
    public ProductKey invalidateUserKey(@RequestHeader long product_key, @RequestBody ProductKey productKey) throws UnauthorizedException{
        return productKeyService.attemptInvalidateUserKey(product_key, productKey);
    }
    @PatchMapping
    public ProductKey invalidateAdminKey(@RequestHeader long product_key, @RequestBody ProductKey productKey) throws UnauthorizedException{
        return productKeyService.attemptInvalidateAdminKey(product_key, productKey);
    }
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleUnauthorized(){

    }
}
