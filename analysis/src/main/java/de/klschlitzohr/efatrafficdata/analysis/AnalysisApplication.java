package de.klschlitzohr.efatrafficdata.analysis;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 11.12.2021
 *
 * @author DerMistkaefer
 */
@SpringBootApplication
@RestController
public class AnalysisApplication {

    @GetMapping("/helloworld")
    public String hello() {
        return "Hello World!";
    }
}
