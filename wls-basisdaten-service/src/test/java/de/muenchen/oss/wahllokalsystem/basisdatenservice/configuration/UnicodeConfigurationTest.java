/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2024
 */
package de.muenchen.oss.wahllokalsystem.basisdatenservice.configuration;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_TEST_PROFILE;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahltag.Wahltag;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahltag.WahltagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.WahlRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.Wahlart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlen.WahlDTO;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

    private static final String WAHLEN_ENDPOINT_URL = "/businessActions/wahlen/";

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
    private WahlRepository wahlRepository;

    @Autowired
    WahltagRepository wahltagRepository;

    @Test
    void testForNfcNormalization() {

        // Persist entity with decomposed string
        // wahltag required for next step to store a list of wahlen
        var searchingForWahltag = new Wahltag("wahltagID", LocalDate.now(), "beschreibung5", "1");
        wahltagRepository.save(searchingForWahltag);
        // create a list of Wahl with only one Wahl containing the TEXT_ATTRIBUTE_DECOMPOSED as 'name'
        val wahlDTOList = createControllerListOfWahlDTO(searchingForWahltag);
        Assertions.assertThat(wahlDTOList.get(0).name()).hasSize(TEXT_ATTRIBUTE_DECOMPOSED.length());
        // store list of Wahl
        testRestTemplate.postForEntity(URI.create(WAHLEN_ENDPOINT_URL + searchingForWahltag.getWahltagID()), wahlDTOList, Void.class);

        // Get the one and only Wahl from repo which now should contain a composed string in the 'name' attribute
        val wahl = wahlRepository.findById("wahlID1").orElseThrow();
        Assertions.assertThat(TEXT_ATTRIBUTE_COMPOSED).isEqualTo(wahl.getName());
        Assertions.assertThat(wahl.getName()).hasSize(TEXT_ATTRIBUTE_COMPOSED.length());
    }

    private List<WahlDTO> createControllerListOfWahlDTO(Wahltag searchingForWahltag) {
        val wahl1 = new de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlen.WahlDTO("wahlID1", TEXT_ATTRIBUTE_DECOMPOSED, 3L, 1L,
                searchingForWahltag.getWahltag(),
                Wahlart.BAW, new Farbe(1, 1, 1), "1");

        return Stream.of(wahl1).filter(wahl -> (wahl.wahltag().equals(searchingForWahltag.getWahltag()))).collect(Collectors.toList());
    }

}
