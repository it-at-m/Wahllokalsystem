/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2024
 */
package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.configuration;

import static de.muenchen.oss.wahllokalsystem.wahlvorstandservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.wahlvorstandservice.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.wahlvorstand.WahlvorstandRepository;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.rest.wahlvorstand.FunktionDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.rest.wahlvorstand.WahlvorstandWriteDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.rest.wahlvorstand.WahlvorstandsmitgliedDTO;
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
    classes = {MicroServiceApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testexample;DB_CLOSE_ON_EXIT=FALSE",
      "refarch.gracefulshutdown.pre-wait-seconds=0"
    })
@ActiveProfiles(
    profiles = {SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE, Profiles.DUMMY_CLIENTS})
class UnicodeConfigurationTest {

  private static final String ENTITY_ENDPOINT_URL = "/businessActions/wahlvorstand/";

  /** Decomposed string: String "Ä-é" represented with unicode letters "A◌̈-e◌́" */
  private static final String TEXT_ATTRIBUTE_DECOMPOSED = "\u0041\u0308-\u0065\u0301";

  /** Composed string: String "Ä-é" represented with unicode letters "Ä-é". */
  private static final String TEXT_ATTRIBUTE_COMPOSED = "\u00c4-\u00e9";

  @Autowired private TestRestTemplate testRestTemplate;

  @Autowired private WahlvorstandRepository wahlvorstandRepository;

  @Test
  void should_returnComposedString_when_givenDecomposedString() {
    val wahlbezirkID = "wahlbezirkID";
    // create a WahlvorstandWriteDTO with a WahlvorstandsmitgliedDTO containing the
    // TEXT_ATTRIBUTE_DECOMPOSED as 'familienname'
    val wahlvorstandsmitgliedDTO =
        new WahlvorstandsmitgliedDTO(
            "identifikator",
            TEXT_ATTRIBUTE_DECOMPOSED,
            "Hans",
            FunktionDTO.B,
            "funktionsname",
            false);
    assertEquals(
        TEXT_ATTRIBUTE_DECOMPOSED.length(),
        wahlvorstandsmitgliedDTO.familienname().length(),
        "The decomposed string lengths should match");
    val wahlvorstandWriteDTO =
        new WahlvorstandWriteDTO(LocalDateTime.now(), Arrays.asList(wahlvorstandsmitgliedDTO));

    testRestTemplate.postForEntity(
        URI.create(ENTITY_ENDPOINT_URL + wahlbezirkID), wahlvorstandWriteDTO, Void.class);

    val wahlvorstand = wahlvorstandRepository.findById(wahlbezirkID);
    assertTrue(wahlvorstand.isPresent(), "Wahlvorstand should be present in the database");
    val mitglied = wahlvorstand.get().getWahlvorstandsmitglieder().get(0);
    assertEquals(
        TEXT_ATTRIBUTE_COMPOSED,
        mitglied.getFamilienname(),
        "The stored name should be in composed form");
    assertEquals(
        TEXT_ATTRIBUTE_COMPOSED.length(),
        mitglied.getFamilienname().length(),
        "The composed string lengths should match");
  }
}
