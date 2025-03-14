/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2024
 */
package de.muenchen.oss.wahllokalsystem.broadcastservice.configuration;

import static de.muenchen.oss.wahllokalsystem.broadcastservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.broadcastservice.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muenchen.oss.wahllokalsystem.broadcastservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.broadcastservice.domain.MessageRepository;
import de.muenchen.oss.wahllokalsystem.broadcastservice.rest.BroadcastMessageDTO;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        classes = { MicroServiceApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
@Slf4j
class UnicodeConfigurationTest {

    private static final String ENTITY_ENDPOINT_URL = "/businessActions/broadcast";

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
    private MessageRepository messageRepository;

    @BeforeEach
    void setup() {
        messageRepository.deleteAll();
    }

    @Test
    void should_returnComposedString_when_givenDecomposedString() {
        List<String> wahlbezirke = Arrays.asList("1", "2", "3", "4");
        val broadcastMessageDTO = new BroadcastMessageDTO(wahlbezirke, TEXT_ATTRIBUTE_DECOMPOSED);

        assertEquals(TEXT_ATTRIBUTE_DECOMPOSED.length(), broadcastMessageDTO.nachricht().length());
        testRestTemplate.postForEntity(URI.create(ENTITY_ENDPOINT_URL), broadcastMessageDTO, Void.class);
        // Check persisted entity contains a composed string via JPA repository.
        val message = messageRepository.findFirstByWahlbezirkIDOrderByEmpfangsZeit("3").orElseThrow();
        Assertions.assertThat(message.getNachricht()).isEqualTo(TEXT_ATTRIBUTE_COMPOSED);
        assertEquals(TEXT_ATTRIBUTE_COMPOSED.length(), message.getNachricht().length());
    }
}
