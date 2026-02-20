/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2024
 */
package de.muenchen.oss.wahllokalsystem.eaiservice.configuration;

import static de.muenchen.oss.wahllokalsystem.eaiservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.eaiservice.TestConstants.SPRING_TEST_PROFILE;

import de.muenchen.oss.wahllokalsystem.eaiservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung.Ergebnismeldung;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung.ErgebnismeldungRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.dto.WahlartDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.ergebnismeldung.dto.AWerteDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.ergebnismeldung.dto.BWerteDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.ergebnismeldung.dto.ErgebnisDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.ergebnismeldung.dto.ErgebnismeldungDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.ergebnismeldung.dto.MeldungsartDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.ergebnismeldung.dto.UngueltigeStimmzettelDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.ergebnismeldung.dto.WahlbriefeWerteDTO;
import java.net.URI;
import java.util.Set;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    classes = {MicroServiceApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testexample;DB_CLOSE_ON_EXIT=FALSE",
      "refarch.gracefulshutdown.pre-wait-seconds=0"
    })
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE})
class UnicodeConfigurationTest {

  private static final String ENTITY_ENDPOINT_URL = "/ergebnismeldung";

  /** Decomposed string: String "Ä-é" represented with unicode letters "A◌̈-e◌́" */
  private static final String TEXT_ATTRIBUTE_DECOMPOSED = "\u0041\u0308-\u0065\u0301";

  /** Composed string: String "Ä-é" represented with unicode letters "Ä-é". */
  private static final String TEXT_ATTRIBUTE_COMPOSED = "\u00c4-\u00e9";

  @Autowired private TestRestTemplate testRestTemplate;

  @Autowired private ErgebnismeldungRepository ergebnismeldungRepository;

  @BeforeEach
  void setup() {
    ergebnismeldungRepository.deleteAll();
  }

  @Test
  void should_returnComposedString_when_givenDecomposedString() {
    // Persist entity with decomposed string.
    final ErgebnismeldungDTO ergebnisMeldungDto = getErgebnismeldungDTO(TEXT_ATTRIBUTE_DECOMPOSED);

    Assertions.assertThat(TEXT_ATTRIBUTE_DECOMPOSED.length())
        .isEqualTo(ergebnisMeldungDto.wahlbezirkID().length());

    // store Ergebnismeldung
    testRestTemplate.postForEntity(URI.create(ENTITY_ENDPOINT_URL), ergebnisMeldungDto, Void.class);

    // Check persisted entity contains a composed string via JPA repository.
    final Ergebnismeldung ergebnismeldung = ergebnismeldungRepository.findAll().iterator().next();
    Assertions.assertThat(TEXT_ATTRIBUTE_COMPOSED).isEqualTo(ergebnismeldung.getWahlbezirkID());
    Assertions.assertThat(TEXT_ATTRIBUTE_COMPOSED.length())
        .isEqualTo(ergebnismeldung.getWahlbezirkID().length());
  }

  private ErgebnismeldungDTO getErgebnismeldungDTO(String wahlbezirkID) {
    val wahlID = "wahlID1";
    val meldungsart = MeldungsartDTO.NIEDERSCHRIFT;
    val aWerte = new AWerteDTO(3L, 2L);
    val bWerte = new BWerteDTO(4L, 3L, 2L);
    val wahlbriefeWerte = new WahlbriefeWerteDTO(3L);
    val ungueltigeStimmzettelDTOList =
        Set.of(
            new UngueltigeStimmzettelDTO("test1", 4L, "wahlvorschlagID1"),
            new UngueltigeStimmzettelDTO("test2", 5L, "wahlvorschlagID2"));
    val ungueltigeStimmzettelAnzahl = 4L;
    val ergebnisse =
        Set.of(
            new ErgebnisDTO("test1", 5L, 3L, "wahlvorschlagID1", "kandidatID1"),
            new ErgebnisDTO("test2", 6L, 4L, "wahlvorschlagID2", "kandidatID2"));
    val wahlart = WahlartDTO.BTW;

    return new ErgebnismeldungDTO(
        wahlbezirkID,
        wahlID,
        meldungsart,
        aWerte,
        bWerte,
        wahlbriefeWerte,
        ungueltigeStimmzettelDTOList,
        ungueltigeStimmzettelAnzahl,
        ergebnisse,
        wahlart);
  }
}
