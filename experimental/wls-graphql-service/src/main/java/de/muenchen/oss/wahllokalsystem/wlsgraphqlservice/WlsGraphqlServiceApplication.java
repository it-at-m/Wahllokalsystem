package de.muenchen.oss.wahllokalsystem.wlsgraphqlservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "de.muenchen.oss.wahllokalsystem.wlsgraphqlservice",
                "de.muenchen.oss.wahllokalsystem.wls.common.exception"
        }
)
public class WlsGraphqlServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WlsGraphqlServiceApplication.class, args);
    }

}
