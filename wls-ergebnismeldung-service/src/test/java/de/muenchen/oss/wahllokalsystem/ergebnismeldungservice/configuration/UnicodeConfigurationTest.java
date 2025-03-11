/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2024
 */
package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung.BegruendungRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.BezirkUndWahlIDStapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.begruendung.BegruendungDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common.BezirkUndWahlIDStapelartDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common.StapelartDTO;
import java.net.URI;
import lombok.val;
import org.assertj.core.api.Assertions;
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

    private static final String ENTITY_ENDPOINT_URL = "/businessActions/begruendung/";

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
    private BegruendungRepository begruendungRepository;

    @Test
    void should_returnComposedString_when_givenDecomposedString() {
        //Persist entity with decomposed string.
        val id = new BezirkUndWahlIDStapelartDTO("bezirkID", "wahlID", StapelartDTO.LTW_BZW_A);
        val begruendungDTO = new BegruendungDTO(id, TEXT_ATTRIBUTE_DECOMPOSED, null, true,
                true);

        Assertions.assertThat(TEXT_ATTRIBUTE_DECOMPOSED.length()).isEqualTo(begruendungDTO.grund().length());

        // store Begruendung
        testRestTemplate.postForEntity(URI.create(ENTITY_ENDPOINT_URL + id.wahlbezirkID() + "/" + id.wahlID() + "/" + id.stapelart()), begruendungDTO,
                Void.class);

        // Check if persisted entity contains a composed string via JPA repository.
        val begruendung = begruendungRepository.findById(new BezirkUndWahlIDStapelart("bezirkID", "wahlID", Stapelart.LTW_BZW_A)).orElse(null);
        Assertions.assertThat(TEXT_ATTRIBUTE_COMPOSED).isEqualTo(begruendung.getGrund1());
        Assertions.assertThat(TEXT_ATTRIBUTE_COMPOSED.length()).isEqualTo(begruendung.getGrund1().length());
    }
}
