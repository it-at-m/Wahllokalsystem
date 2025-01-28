/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2024
 */
package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerteRepository;
import org.junit.jupiter.api.Disabled;
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

    private static final String ENTITY_ENDPOINT_URL = "/theEntities";

    /**
     * Decomposed string: String "Ä-é" represented with unicode letters "A◌̈-e◌́"
     */
    private static final String TEXT_ATTRIBUTE_DECOMPOSED = "\u0041\u0308-\u0065\u0301";

    /**
     * Composed string: String "Ä-é" represented with unicode letters "Ä-é".
     */
    private static final String TEXT_ATTRIBUTE_COMPOSED = "\u00c4-\u00e9";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private AWerteRepository aWerteRepository;

    @Test
    @Disabled
    void should_returnComposedString_when_givenDecomposedString() {
        // Persist entity with decomposed string.
        //        final TheEntityDto theEntityDto = new TheEntityDto();
        //        theEntityDto.setTextAttribute(TEXT_ATTRIBUTE_DECOMPOSED);
        //        assertEquals(TEXT_ATTRIBUTE_DECOMPOSED.length(), theEntityDto.getTextAttribute().length());
        //        final TheEntityDto response = testRestTemplate.postForEntity(URI.create(ENTITY_ENDPOINT_URL), theEntityDto, TheEntityDto.class).getBody();
        //
        //        // Check whether response contains a composed string.
        //        assertEquals(TEXT_ATTRIBUTE_COMPOSED, response.getTextAttribute());
        //        assertEquals(TEXT_ATTRIBUTE_COMPOSED.length(), response.getTextAttribute().length());
        //
        //        // Extract uuid from self link.
        //        final UUID uuid = UUID.fromString(StringUtils.substringAfterLast(response.getRequiredLink("self").getHref(), "/"));
        //
        //        // Check persisted entity contains a composed string via JPA repository.
        //        final AWerte theEntity = aWerteRepository.findById(uuid).orElse(null);
        //        assertEquals(TEXT_ATTRIBUTE_COMPOSED, theEntity.getTextAttribute());
        //        assertEquals(TEXT_ATTRIBUTE_COMPOSED.length(), theEntity.getTextAttribute().length());
    }

}
