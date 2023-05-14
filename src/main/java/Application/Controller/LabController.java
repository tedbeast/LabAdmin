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
    @Autowired
    LabService labService;
    @GetMapping(value = "/lab/saved/{name}", produces="application/zip")
    public ResponseEntity getSavedLab(@RequestHeader long product_key, @PathVariable String name) throws IOException, InterruptedException, UnauthorizedException, LabZipException {
        LabSaved labSaved = labService.getSavedLab(product_key, name);
        ByteArrayResource byteArrayResource = new ByteArrayResource(labSaved.getZip());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(byteArrayResource.contentLength());
        return new ResponseEntity(byteArrayResource, headers, HttpStatus.OK);
    }
    @GetMapping(value = "/lab/canonical/{name}", produces="application/zip")
    public ResponseEntity getCanonicalLab(@RequestHeader long product_key, @PathVariable String name) throws IOException, InterruptedException, LabZipException, UnauthorizedException, LabRetrievalException {
        LabCanonical labCanonical = labService.getCanonicalLab(product_key, name);
        ByteArrayResource byteArrayResource = new ByteArrayResource(labCanonical.getZip());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(byteArrayResource.contentLength());
        return new ResponseEntity(byteArrayResource, headers, HttpStatus.OK);
    }
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleUnauthorized(){

    }
}