/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2024
 */
package de.muenchen.oss.wahllokalsystem.wahlvorstandservice;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.mapping.BWBFunktionsnamenMapping;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.mapping.UWBFunktionsnamenMapping;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Application class for starting the micro-service.
 */

@EntityScan(
        basePackages = {
                "org.springframework.data.jpa.convert.threeten",
                "de.muenchen.oss.wahllokalsystem.wahlvorstandservice"
        }
)
@EnableJpaRepositories(
        basePackages = {
                "de.muenchen.oss.wahllokalsystem.wahlvorstandservice"
        }
)
@SpringBootApplication(
        scanBasePackages = {
                "org.springframework.data.jpa.convert.threeten",
                "de.muenchen.oss.wahllokalsystem.wahlvorstandservice",
                "de.muenchen.oss.wahllokalsystem.wls.common.exception",
                "de.muenchen.oss.wahllokalsystem.wls.common.security"
        }
)
public class MicroServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroServiceApplication.class, args);
    }

    @Bean
    @ConfigurationProperties
    public UWBFunktionsnamenMapping uwbFunktionsnamenMapping() {
        return new UWBFunktionsnamenMapping();
    }

    @Bean
    @ConfigurationProperties
    public BWBFunktionsnamenMapping bwbFunktionsnamenMapping() {
        return new BWBFunktionsnamenMapping();
    }
}
