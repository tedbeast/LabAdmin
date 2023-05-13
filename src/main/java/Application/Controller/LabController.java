package Application.Controller;

import Application.Exception.UnauthorizedException;
import Application.Service.LabService;
import jdk.jfr.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
public class LabController {
    @Autowired
    LabService labService;
    @GetMapping(value = "/lab/{name}", produces="application/zip")
    public ResponseEntity getLab(@RequestHeader long product_key, @PathVariable String name) throws IOException, InterruptedException {
        labService.getLabZip(name);
        String filePath = "./"+name+".zip";
        InputStream inputStream = new FileInputStream(new File(filePath));
        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(Files.size(Paths.get(filePath)));
        return new ResponseEntity(inputStreamResource, headers, HttpStatus.OK);

    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleUnauthorized(){

    }
}