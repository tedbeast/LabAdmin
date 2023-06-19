package com.revature.LabAdmin.Controller;

import com.revature.LabAdmin.Exception.KeyException;
import com.revature.LabAdmin.Exception.UnauthorizedException;
import com.revature.LabAdmin.Model.ProductKey;
import com.revature.LabAdmin.Service.ProductKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
@RestController
public class ProductKeyController {
    @Autowired
    public ProductKeyService productKeyService;
    @PostMapping("key")
    public ProductKey genNewUserKey(@RequestHeader long auth_key, @RequestBody ProductKey productKey) throws UnauthorizedException {
        return productKeyService.attemptKeyGeneration(auth_key, productKey);
    }
    @PostMapping("key/{id}/key-invalidate-action")
    public ProductKey invalidateUserKey(@RequestHeader long product_key, @RequestParam long id) throws UnauthorizedException, KeyException {
        return productKeyService.attemptKeyInvalidation(product_key, id);
    }
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleUnauthorized(){

    }
    @ExceptionHandler(KeyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleKeyException(){

    }
}
