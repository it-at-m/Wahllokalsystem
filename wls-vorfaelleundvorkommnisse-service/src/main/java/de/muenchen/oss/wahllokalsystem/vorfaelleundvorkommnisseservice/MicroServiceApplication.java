/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2024
 */
package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Application class for starting the micro-service.
 */
@EntityScan(
        basePackages = {
                "org.springframework.data.jpa.convert.threeten",
                "de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice"
        }
)
@EnableJpaRepositories(
        basePackages = {
                "de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice"
        }
)
// todo: springbootapplication statt @enableautoconfig, @config und @componentscan ? --> https://stackoverflow.com/questions/33619532/configuration-using-annotation-springbootapplication
@SpringBootApplication(scanBasePackages = {
        "org.springframework.data.jpa.convert.threeten",
        "de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice",
        "de.muenchen.oss.wahllokalsystem.wls.common.exception"
    }
)
public class MicroServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroServiceApplication.class, args);
    }

}
