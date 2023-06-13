package Application;

import Application.Exception.UnauthorizedException;
import Application.Service.CMDService;
import Application.Service.ProductKeyService;
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
        CMDService cmdService = appContext.getBean(CMDService.class);
//        git requires some config in order to do any operations - no other place to configure that, really
        cmdService.runCommandReturnOutput("git config --global user.name \"labadmin\"");
        cmdService.runCommandReturnOutput("git config --global user.password \"samplepass\"");
        cmdService.runCommandReturnOutput("git config --global user.email \"sample@sample.com\"");
    }
}
