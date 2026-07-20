package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahllokalZustand;

import de.muenchen.oss.wahllokalsystem.eaiservice.Authorities;
import de.muenchen.oss.wahllokalsystem.eaiservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.eaiservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.dto.WahllokalZustandDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDateTime;
import java.util.Collections;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({TestConstants.SPRING_TEST_PROFILE})
public class WahllokalZustandServiceSecurityTest {

  @Autowired WahllokalZustandService wahllokalZustandService;

  @Nested
  class SetWahllokalZustand {

    @Test
    void should_notThrowAnyException_when_allAuthoritiesAreGiven() {
      SecurityUtils.runWith(Authorities.SERVICE_SAVE_WAHLLOKALZUSTAND);

      Assertions.assertThatNoException()
          .isThrownBy(
              () ->
                  wahllokalZustandService.setWahllokalZustand(
                      new WahllokalZustandDTO(
                          "2853ba2d-baaa-49ee-93f7-a653d17d6a72",
                          null,
                          null,
                          Collections.emptySet())));
    }

    @Test
    void should_throwAccessDeniedException_when_anyAuthorityMissing() {
      SecurityUtils.runWith();

      Assertions.assertThatThrownBy(
              () ->
                  wahllokalZustandService.setWahllokalZustand(
                      new WahllokalZustandDTO(
                          "2853ba2d-baaa-49ee-93f7-a653d17d6a72",
                          null,
                          null,
                          Collections.emptySet())))
          .isInstanceOf(AccessDeniedException.class);
    }
  }

  @Nested
  class SetWahllokalZustandLastSeen {

    @Test
    void should_notThrowAnyException_when_allAuthoritiesAreGiven() {
      SecurityUtils.runWith(Authorities.SERVICE_SAVE_WAHLLOKALZUSTAND);
      Assertions.assertThatNoException()
          .isThrownBy(
              () ->
                  wahllokalZustandService.setWahllokalZustandLastSeen(
                      "2853ba2d-baaa-49ee-93f7-a653d17d6a72", "A", LocalDateTime.now()));
    }

    @Test
    void should_throwAccessDeniedException_when_anyAuthorityMissing() {
      SecurityUtils.runWith();

      Assertions.assertThatThrownBy(
              () ->
                  wahllokalZustandService.setWahllokalZustandLastSeen(
                      "2853ba2d-baaa-49ee-93f7-a653d17d6a72", "A", null))
          .isInstanceOf(AccessDeniedException.class);
    }
  }

  @Nested
  class SetWahllokalZustandLetzteAbmeldung {

    @Test
    void should_notThrowAnyException_when_allAuthoritiesAreGiven() {
      SecurityUtils.runWith(Authorities.SERVICE_SAVE_WAHLLOKALZUSTAND);

      Assertions.assertThatNoException()
          .isThrownBy(
              () ->
                  wahllokalZustandService.setWahllokalZustandLastSeen(
                      "2853ba2d-baaa-49ee-93f7-a653d17d6a72", "A", LocalDateTime.now()));
    }

    @Test
    void should_throwAccessDeniedException_when_anyAuthorityMissing() {
      SecurityUtils.runWith();

      Assertions.assertThatThrownBy(
              () ->
                  wahllokalZustandService.setWahllokalZustandLetzteAbmeldung(
                      "2853ba2d-baaa-49ee-93f7-a653d17d6a72", "A", null))
          .isInstanceOf(AccessDeniedException.class);
    }
  }
}
