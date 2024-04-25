/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2024
 */
package de.muenchen.oss.wahllokalsystem.broadcastservice.configuration;

import de.muenchen.oss.wahllokalsystem.broadcastservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.broadcastservice.domain.Message;
import de.muenchen.oss.wahllokalsystem.broadcastservice.domain.MessageRepository;
import de.muenchen.oss.wahllokalsystem.broadcastservice.rest.BroadcastMessageDTO;
import de.muenchen.oss.wahllokalsystem.broadcastservice.rest.MessageDTO;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;

import static de.muenchen.oss.wahllokalsystem.broadcastservice.TestConstants.SPRING_TEST_PROFILE;
import static de.muenchen.oss.wahllokalsystem.broadcastservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
        classes = { MicroServiceApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:testexample;DB_CLOSE_ON_EXIT=FALSE",
                "refarch.gracefulshutdown.pre-wait-seconds=0"
        }
)
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
@Slf4j
class UnicodeConfigurationTest {

    private static final String ENTITY_ENDPOINT_URL = "/businessActions/broadcast";
    private static final String GET_MESSAGE_PATH = "/businessActions/getMessage/" + "3";

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

    @Test
    void testForNfcNormalization() {
        List<String> wahlbezirke = Arrays.asList("1", "2", "3", "4");
        val broadcastMessageDTO = new BroadcastMessageDTO(wahlbezirke, TEXT_ATTRIBUTE_DECOMPOSED);

        assertEquals(TEXT_ATTRIBUTE_DECOMPOSED.length(), broadcastMessageDTO.nachricht().length());
        testRestTemplate.postForEntity(URI.create(ENTITY_ENDPOINT_URL), broadcastMessageDTO, Void.class);
        // Check persisted entity contains a composed string via JPA repository.
        final Message message = messageRepository.findFirstByWahlbezirkIDOrderByEmpfangsZeit("3").orElseThrow();
        Assertions.assertThat(message.getNachricht()).isEqualTo(TEXT_ATTRIBUTE_COMPOSED);
        assertEquals(TEXT_ATTRIBUTE_COMPOSED.length(), message.getNachricht().length());
    }

}
