package Application.Controller;

import Application.Exception.UnauthorizedException;
import Application.Model.ProductKey;
import Application.Service.ProductKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
@RestController
public class AdminController {
    @Autowired
    public ProductKeyService productKeyService;
    @GetMapping("key/new/user")
    public ProductKey genNewUserKey(@RequestHeader long product_key) throws UnauthorizedException {
        return productKeyService.attemptUserKeyGeneration(product_key);
    }
    @GetMapping("key/new/admin")
    public ProductKey genNewAdminKey(@RequestHeader long product_key) throws UnauthorizedException {
        return productKeyService.attemptAdminKeyGeneration(product_key);
    }
    @PatchMapping("key/invalidate/user")
    public ProductKey invalidateUserKey(@RequestHeader long product_key, @RequestBody ProductKey productKey) throws UnauthorizedException{
        return productKeyService.attemptInvalidateUserKey(product_key, productKey);
    }
    @PatchMapping("key/invalidate/admin")
    public ProductKey invalidateAdminKey(@RequestHeader long product_key, @RequestBody ProductKey productKey) throws UnauthorizedException{
        return productKeyService.attemptInvalidateAdminKey(product_key, productKey);
    }
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleUnauthorized(){

    }
}
