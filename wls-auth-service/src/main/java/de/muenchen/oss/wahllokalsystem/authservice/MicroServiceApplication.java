/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2024
 */
package de.muenchen.oss.wahllokalsystem.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Application class for starting the micro-service.
 */
@Configuration
@ComponentScan(
        basePackages = {
                "org.springframework.data.jpa.convert.threeten",
                "de.muenchen.oss.wahllokalsystem.authservice"
        }
)
@EntityScan(
        basePackages = {
                "org.springframework.data.jpa.convert.threeten",
                "de.muenchen.oss.wahllokalsystem.authservice"
        }
)
@EnableJpaRepositories(
        basePackages = {
                "de.muenchen.oss.wahllokalsystem.authservice"
        }
)
@EnableAutoConfiguration
public class MicroServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroServiceApplication.class, args);
    }

}