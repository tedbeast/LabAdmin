package Application.Controller;

import Application.Exception.LabRetrievalException;
import Application.Exception.LabZipException;
import Application.Exception.UnauthorizedException;
import Application.Model.LabCanonical;
import Application.Model.LabSaved;
import Application.Service.LabService;
import jdk.jfr.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
public class LabController {
    LabService labService;
    @Autowired
    public LabController(LabService labService){
        this.labService = labService;
    }
    @GetMapping(value = "/lab/{name}", produces="application/zip")
    public ResponseEntity getSavedLab(@RequestHeader long product_key, @PathVariable String name) throws IOException, InterruptedException, UnauthorizedException, LabZipException {
        LabSaved labSaved = labService.getSavedLab(product_key, name);
        ByteArrayResource byteArrayResource = new ByteArrayResource(labSaved.getZip());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(byteArrayResource.contentLength());
        return new ResponseEntity(byteArrayResource, headers, HttpStatus.OK);
    }
    @PatchMapping(value = "lab/reset/{name}")
    public void resetUserLab(@RequestHeader long product_key, @PathVariable String name){
        LabSaved labSaved = labService.resetLabProgress(product_key, name);
    }
    @PatchMapping(value = "lab/save/{name}")
    public void saveUserLab(@RequestHeader long product_key, @PathVariable String name, @RequestBody byte[] zip){
        LabSaved labSaved = labService.saveLabProgress(product_key, name, zip);
    }
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleUnauthorized(){

    }
}