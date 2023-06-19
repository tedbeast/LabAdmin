package com.revature.LabAdmin.Controller;

import com.revature.LabAdmin.Exception.LabRetrievalException;
import com.revature.LabAdmin.Exception.LabZipException;
import com.revature.LabAdmin.Exception.UnauthorizedException;
import com.revature.LabAdmin.Model.LabCanonical;
import com.revature.LabAdmin.Model.LabSaved;
import com.revature.LabAdmin.Service.LabCanonicalService;
import com.revature.LabAdmin.Service.LabSavedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.List;

@RestController
public class LabController {
    LabSavedService labSavedService;
    LabCanonicalService labCanonicalService;
    @Autowired
    public LabController(LabSavedService labSavedService, LabCanonicalService labCanonicalService){
        this.labSavedService = labSavedService;
        this.labCanonicalService = labCanonicalService;
    }

    /**
     * for when the frontend requests a lab with a certain name
     * @param product_key
     * @param name
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws UnauthorizedException
     * @throws LabZipException
     */
    @GetMapping(value = "/lab/{name}", produces="application/zip")
    public ResponseEntity getSavedLab(@RequestHeader long product_key, @PathVariable String name) throws IOException, InterruptedException, UnauthorizedException, LabZipException, LabRetrievalException {
        ByteArrayResource byteArrayResource = labSavedService.getSavedLab(product_key, name);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(byteArrayResource.contentLength());
        return new ResponseEntity(byteArrayResource, headers, HttpStatus.OK);
    }

    @GetMapping("lab-canonical")
    public List<LabCanonical> getLabs(@RequestHeader long product_key) throws UnauthorizedException {
        return labCanonicalService.getAllCanonicals(product_key);
    }
    @PostMapping("lab-canonical")
    public List<LabCanonical> registerLab(@RequestHeader long product_key) throws UnauthorizedException {
        return null;
    }
    @DeleteMapping("lab-canonical")
    public List<LabCanonical> deleteLab(@RequestHeader long product_key) throws UnauthorizedException {
        return null;
    }

    /**
     * for when the frontend requests a reset of a lab
     * @param product_key
     * @param name
     */
    @PostMapping(value = "reset-lab-action/{name}")
    public void resetUserLab(@RequestHeader long product_key, @PathVariable String name) throws LabZipException, LabRetrievalException, IOException, InterruptedException, UnauthorizedException {
        LabSaved labSaved = labSavedService.resetLabProgress(product_key, name);
    }

    /**
     * for when the frontend sends a lab to be saved
     * @param product_key
     * @param name
     * @param zip
     */
    @PostMapping(value = "save-lab-action/{name}")
    public void saveUserLab(@RequestHeader long product_key, @PathVariable String name, @RequestBody byte[] zip) throws UnauthorizedException {
        LabSaved labSaved = labSavedService.saveLabProgress(product_key, name, zip);
    }

    /**
     * for when the auth key is invalid
     */
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleUnauthorized(){

    }
    /**
     * for when a lab is unretrievable
     */
    @ExceptionHandler(LabRetrievalException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleUnretrievable(){

    }
    /**
     * for when a zipping issue occurs
     */
    @ExceptionHandler(LabZipException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleUnzippable(){

    }
}