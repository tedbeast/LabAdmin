package com.revature.LabAdmin;

import com.revature.LabAdmin.Exception.UnauthorizedException;
import com.revature.LabAdmin.Service.CMDService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class App {
    /**
     * Logger setup
     */
    public static Logger log = LogManager.getLogger();
    public static void main(String[] args) throws UnauthorizedException, IOException, InterruptedException {
        ApplicationContext appContext = SpringApplication.run(App.class);
    }
}
