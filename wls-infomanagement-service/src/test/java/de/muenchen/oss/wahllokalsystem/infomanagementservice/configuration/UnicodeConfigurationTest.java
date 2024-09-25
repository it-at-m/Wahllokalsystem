/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2024
 */
package de.muenchen.oss.wahllokalsystem.infomanagementservice.configuration;

import static de.muenchen.oss.wahllokalsystem.infomanagementservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.infomanagementservice.TestConstants.SPRING_TEST_PROFILE;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.MicroServiceApplication;

import java.net.URI;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.konfiguration.KonfigurationRepository;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.konfiguration.dto.KonfigurationDTO;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        classes = { MicroServiceApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:testexample;DB_CLOSE_ON_EXIT=FALSE",
                "refarch.gracefulshutdown.pre-wait-seconds=0"
        }
)
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
class UnicodeConfigurationTest {

    private static final String ENTITY_ENDPOINT_URL = "/businessActions/konfiguration/WILLKOMMENSTEXT";

    /**
     * Decomposed string:
     * String "Ä-é" represented with unicode letters "A◌̈-e◌́"
     */
    private static final String TEXT_ATTRIBUTE_DECOMPOSED = "\u0041\u0308-\u0065\u0301";

    /**
     * Composed string:
     * String "Ä-é" represented with unicode letters "Ä-é".
     */
    private static final String TEXT_ATTRIBUTE_COMPOSED = "\u00c4-\u00e9";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private KonfigurationRepository konfigurationRepository;

    @Test
    void testForNfcNormalization() {
        val konfigurationDTO = KonfigurationDTO.builder().schluessel("WILLKOMMENSTEXT").beschreibung(TEXT_ATTRIBUTE_DECOMPOSED).build();

        Assertions.assertThat(konfigurationDTO.beschreibung()).hasSize(TEXT_ATTRIBUTE_DECOMPOSED.length());
        testRestTemplate.postForEntity(URI.create(ENTITY_ENDPOINT_URL), konfigurationDTO, Void.class);
        // Check persisted entity contains a composed string via JPA repository.
        val konfiguration = konfigurationRepository.findById("WILLKOMMENSTEXT").orElseThrow();
        Assertions.assertThat(konfiguration.getBeschreibung()).isEqualTo(TEXT_ATTRIBUTE_COMPOSED);
        Assertions.assertThat(konfiguration.getBeschreibung()).hasSize(TEXT_ATTRIBUTE_COMPOSED.length());
    }
}
