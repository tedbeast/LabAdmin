import Application.App;
import Application.Service.CMDService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest(properties = "spring.main.lazy-initialization=true",  classes = {App.class})
public class CMDServiceTest {
    @Autowired
    CMDService cmdService;
    @Test
    public void testReturnedOutput() throws IOException, InterruptedException {
        String result = cmdService.runCommandReturnOutput("git --help");
        Assertions.assertTrue(result.contains("These are common Git commands used in various situations:"));
    }
}
