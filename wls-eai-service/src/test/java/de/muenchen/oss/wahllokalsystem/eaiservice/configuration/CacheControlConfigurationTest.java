/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2024
 */
package de.muenchen.oss.wahllokalsystem.eaiservice.configuration;

import static de.muenchen.oss.wahllokalsystem.eaiservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.eaiservice.TestConstants.SPRING_TEST_PROFILE;

import de.muenchen.oss.wahllokalsystem.eaiservice.MicroServiceApplication;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(
    classes = {MicroServiceApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testexample;DB_CLOSE_ON_EXIT=FALSE",
      "refarch.gracefulshutdown.pre-wait-seconds=0"
    })
@AutoConfigureRestTestClient
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE})
class CacheControlConfigurationTest {

  private static final String ENTITY_ENDPOINT_URL = "/theEntities";

  private static final String EXPECTED_CACHE_CONTROL_HEADER_VALUES =
      "no-cache, no-store, must-revalidate";

  @Autowired private RestTestClient restTestClient;

  @Test
  @Disabled
  void should_returnExpectedHeaderValues_when_givenEndpoint() {
    restTestClient
        .get()
        .uri(ENTITY_ENDPOINT_URL)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .valueEquals(HttpHeaders.CACHE_CONTROL, EXPECTED_CACHE_CONTROL_HEADER_VALUES);
  }
}
