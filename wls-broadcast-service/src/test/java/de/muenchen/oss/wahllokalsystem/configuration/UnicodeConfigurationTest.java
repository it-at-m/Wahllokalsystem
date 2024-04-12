/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2024
 */
package de.muenchen.oss.wahllokalsystem.configuration;

import de.muenchen.oss.wahllokalsystem.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.domain.Message;
import de.muenchen.oss.wahllokalsystem.domain.MessageRepository;
import de.muenchen.oss.wahllokalsystem.rest.MessageDTO;
import java.time.LocalDateTime;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.util.UUID;

import static de.muenchen.oss.wahllokalsystem.TestConstants.SPRING_TEST_PROFILE;
import static de.muenchen.oss.wahllokalsystem.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.TestConstants.TheEntityDto;
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
class UnicodeConfigurationTest {

    private static final String ENTITY_ENDPOINT_URL = "/theEntities";

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
        // Persist entity with decomposed string.
        final MessageDTO messageDTO = new MessageDTO(UUID.fromString("1-2-3-4-5"), "wahlbezirkID_1", TEXT_ATTRIBUTE_DECOMPOSED,  LocalDateTime.of(1990, 10, 3, 2, 47));
        final TheEntityDto theEntityDto = new TheEntityDto();
        theEntityDto.setMyMessageDTO(messageDTO);

        assertEquals(TEXT_ATTRIBUTE_DECOMPOSED.length(), theEntityDto.getMyMessageDTO().nachricht().length());
        final TheEntityDto response = testRestTemplate.postForEntity(URI.create(ENTITY_ENDPOINT_URL), theEntityDto.getMyMessageDTO(), TheEntityDto.class).getBody();

        // Check whether response contains a composed string.
        assert response != null;
        assertEquals(TEXT_ATTRIBUTE_COMPOSED, response.getMyMessageDTO().nachricht());
        assertEquals(TEXT_ATTRIBUTE_COMPOSED.length(), response.getMyMessageDTO().nachricht().length());

        // Extract uuid from self link.
        final UUID uuid = UUID.fromString(StringUtils.substringAfterLast(response.getRequiredLink("self").getHref(), "/"));

        // Check persisted entity contains a composed string via JPA repository.
        final Message message = messageRepository.findById(uuid).orElse(null);
        assertEquals(TEXT_ATTRIBUTE_COMPOSED, message.getNachricht());
        assertEquals(TEXT_ATTRIBUTE_COMPOSED.length(), message.getNachricht().length());
    }

}
