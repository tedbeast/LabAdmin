package Application;

import Application.Exception.UnauthorizedException;
import Application.Service.ProductKeyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application {
    /**
     * Logger setup
     */
    public static Logger log = LogManager.getLogger();
    public static void main(String[] args) throws UnauthorizedException {
        ApplicationContext appContext = SpringApplication.run(Application.class);
        /**
         * just for testing!! remove this later!!
         */
        log.warn("warning- debugging superadmin key not removed!");
        ProductKeyService productKeyService = appContext.getBean(ProductKeyService.class);
        productKeyService.superAdminKeyGeneration(7191999L);
    }
}
