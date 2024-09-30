/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2024
 */
package de.muenchen.oss.wahllokalsystem.briefwahlservice.configuration;

import static de.muenchen.oss.wahllokalsystem.briefwahlservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.briefwahlservice.TestConstants.SPRING_TEST_PROFILE;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.common.beanstandetewahlbriefe.Zurueckweisungsgrund;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.domain.BeanstandeteWahlbriefeRepository;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.rest.beanstandetewahlbriefe.BeanstandeteWahlbriefeDTO;
import java.net.URI;
import java.util.Map;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.util.Streamable;
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

    private static final String BEANSTANDETE_WAHLBRIEFE_ENDPOINT_URL = "/businessActions/beanstandeteWahlbriefe/";

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
    private BeanstandeteWahlbriefeRepository beanstandeteWahlbriefeRepository;

    @Test
    void testForNfcNormalization() {
        // Persist entity with decomposed string.
        val wahlbezirkID = "wahlbezirkID";
        val waehlerVerzeichnisNummer = 1L;
        val key1 = "key1";
        val key2 = "key2";
        val beanstandeteWahlbriefeDTO = createControllerBeanstandeteWahlbriefeDTO(wahlbezirkID, waehlerVerzeichnisNummer, key1, key2, TEXT_ATTRIBUTE_DECOMPOSED);
        Assertions.assertThat((String)beanstandeteWahlbriefeDTO.beanstandeteWahlbriefe().keySet().toArray()[0]).hasSize(key1.length() + TEXT_ATTRIBUTE_DECOMPOSED.length());
        Assertions.assertThat((String)beanstandeteWahlbriefeDTO.beanstandeteWahlbriefe().keySet().toArray()[1]).hasSize(key2.length() + TEXT_ATTRIBUTE_DECOMPOSED.length());

        testRestTemplate.postForEntity(URI.create(BEANSTANDETE_WAHLBRIEFE_ENDPOINT_URL + wahlbezirkID + "/" + waehlerVerzeichnisNummer), beanstandeteWahlbriefeDTO, Void.class);

        val beantstandeteWahlbriefeInRepo = Streamable.of(beanstandeteWahlbriefeRepository.findAll()).toList();
        Assertions.assertThat(beantstandeteWahlbriefeInRepo).hasSize(1);
        Assertions.assertThat(beantstandeteWahlbriefeInRepo.get(0).getBeanstandeteWahlbriefe().keySet().toArray()[0]).isEqualTo(key1 + TEXT_ATTRIBUTE_COMPOSED);
        Assertions.assertThat(beantstandeteWahlbriefeInRepo.get(0).getBeanstandeteWahlbriefe().keySet().toArray()[1]).isEqualTo(key2 + TEXT_ATTRIBUTE_COMPOSED);
    }

    private BeanstandeteWahlbriefeDTO createControllerBeanstandeteWahlbriefeDTO(String wahlbezirkID, Long waehlerverzeichnisNummer, String key1, String key2, String textAttributeDecomposed) {
        return new BeanstandeteWahlbriefeDTO(wahlbezirkID, waehlerverzeichnisNummer,
                Map.of(key1 + textAttributeDecomposed, new Zurueckweisungsgrund[]{Zurueckweisungsgrund.NICHT_WAHLBERECHTIGT, Zurueckweisungsgrund.GEGENSTAND_IM_UMSCHLAG},
                        key2 + textAttributeDecomposed, new Zurueckweisungsgrund[]{Zurueckweisungsgrund.UNTERSCHRIFT_FEHLT, Zurueckweisungsgrund.LOSE_STIMMZETTEL})
        );
    }

}
