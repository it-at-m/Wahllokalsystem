package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahllokalZustand;

import de.muenchen.oss.wahllokalsystem.eaiservice.Authorities;
import de.muenchen.oss.wahllokalsystem.eaiservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.eaiservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.dto.WahllokalZustandDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.Collections;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
      SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_SETWAHLLOKALZUSTAND);

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

    @ParameterizedTest(name = "{index} - {1} missing")
    @MethodSource("getMissingAuthoritiesVariations")
    void should_throwAccessDeniedException_when_anyAuthorityMissing(
        final ArgumentsAccessor argumentsAccessor) {
      SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));

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

    private static Stream<Arguments> getMissingAuthoritiesVariations() {
      return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(
          Authorities.ALL_AUTHORITIES_SETWAHLLOKALZUSTAND);
    }
  }
}
