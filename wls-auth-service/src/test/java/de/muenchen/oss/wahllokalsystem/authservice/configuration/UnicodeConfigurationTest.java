/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2024
 */
package de.muenchen.oss.wahllokalsystem.authservice.configuration;

import static de.muenchen.oss.wahllokalsystem.authservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.authservice.TestConstants.SPRING_TEST_PROFILE;

import de.muenchen.oss.wahllokalsystem.authservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.authservice.domain.UserRepository;
import de.muenchen.oss.wahllokalsystem.authservice.rest.WahlbezirksartDTO;
import de.muenchen.oss.wahllokalsystem.authservice.rest.WahllokalUserInfoDTO;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

  private static final String AUTH_ENDPOINT_URL = "/generateAndExportWahllokalbenutzer/";

  /** Decomposed string: String "Ä-é" represented with unicode letters "A◌̈-e◌́" */
  private static final String TEXT_ATTRIBUTE_DECOMPOSED = "\u0041\u0308-\u0065\u0301";

  /** Composed string: String "Ä-é" represented with unicode letters "Ä-é". */
  private static final String TEXT_ATTRIBUTE_COMPOSED = "\u00c4-\u00e9";

  @Autowired private TestRestTemplate testRestTemplate;

  @Autowired private UserRepository userRepository;

  @Value("${service.config.user.sizeOfTeam:5}")
  int sizeOfTeam;

  @Test
  void should_testForNfcNormalization_when_givenComposedString() {
    val wahltagID = "wahltagID";
    val wahllokalUser =
        new WahllokalUserInfoDTO(
            "wahlbezirknummer",
            LocalDate.now(),
            "wbzID",
            WahlbezirksartDTO.UWB,
            TEXT_ATTRIBUTE_DECOMPOSED);

    testRestTemplate.postForEntity(
        URI.create(AUTH_ENDPOINT_URL + wahltagID), List.of(wahllokalUser), Void.class);

    val users = userRepository.findByWahltagID(wahltagID);
    Assertions.assertThat(users).hasSize(sizeOfTeam);
    Assertions.assertThat(users.stream().findFirst().get().getWbid_wahlnummer())
        .isEqualTo(TEXT_ATTRIBUTE_COMPOSED);
  }
}
