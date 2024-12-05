/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2024
 */
package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.configuration;

import static de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.EreignisRepository;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis.EreignisDTO;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis.EreignisartDTO;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis.EreignisseWriteDTO;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;
import lombok.val;
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

    private static final String ENTITY_ENDPOINT_URL = "/businessActions/ereignisse/";

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
    private EreignisRepository ereignisRepository;

    @Test
    void should_returnComposedString_when_givenDecomposedString() {
        // Persist entity with decomposed string.
        val wahlbezirkID = "wahlbezirkID01";
        // create a list of Ereignisse with only one Ereignis containing the TEXT_ATTRIBUTE_DECOMPOSED as 'beschreibung'
        val ereignisDTO = new EreignisDTO(TEXT_ATTRIBUTE_DECOMPOSED, LocalDateTime.now().withNano(0), EreignisartDTO.VORFALL);
        assertEquals(TEXT_ATTRIBUTE_DECOMPOSED.length(), ereignisDTO.beschreibung().length());
        val ereignisseWriteDTO = new EreignisseWriteDTO(Arrays.asList(ereignisDTO));

        // store list of Ereignisse
        testRestTemplate.postForEntity(URI.create(ENTITY_ENDPOINT_URL + wahlbezirkID), ereignisseWriteDTO,
                Void.class);

        // Check persisted entity contains a composed string via JPA repository.
        val ereignis = ereignisRepository.findByWahlbezirkID(wahlbezirkID);
        assertEquals(TEXT_ATTRIBUTE_COMPOSED, ereignis.get(0).getBeschreibung());
        assertEquals(TEXT_ATTRIBUTE_COMPOSED.length(), ereignis.get(0).getBeschreibung().length());
    }
}
