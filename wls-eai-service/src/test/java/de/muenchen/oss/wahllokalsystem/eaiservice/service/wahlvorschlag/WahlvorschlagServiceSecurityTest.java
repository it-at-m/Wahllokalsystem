package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlvorschlag;

import de.muenchen.oss.wahllokalsystem.eaiservice.Authorities;
import de.muenchen.oss.wahllokalsystem.eaiservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.eaiservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.eaiservice.exception.NoSearchResultFoundException;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({TestConstants.SPRING_TEST_PROFILE})
public class WahlvorschlagServiceSecurityTest {

  @Autowired WahlvorschlagService wahlvorschlagService;

  @Nested
  class GetWahlvorschlaegeForWahlAndWahlbezirk {

    @Test
    void should_notThrowException_when_givenAllAuthorities() {
      SecurityUtils.runWith(Authorities.SERVICE_LOAD_WAHLVORSCHLAEGE);

      Assertions.assertThatException()
          .isThrownBy(
              () ->
                  wahlvorschlagService.getWahlvorschlaegeForWahlAndWahlbezirk(
                      "wahlID", "wahlbezirkID"))
          .isInstanceOf(NoSearchResultFoundException.class);
    }

    @Test
    void should_throwAccessDeniedException_when_anyAuthorityMissing() {
      SecurityUtils.runWith();

      Assertions.assertThatThrownBy(
              () ->
                  wahlvorschlagService.getWahlvorschlaegeForWahlAndWahlbezirk(
                      "wahlID", "wahlbezirkID"))
          .isInstanceOf(AccessDeniedException.class);
    }
  }

  @Nested
  class GetWahlvorschlaegeListeForWahltagAndWahlID {

    @Test
    void should_notThrowException_when_givenAllAuthorities() {
      SecurityUtils.runWith(Authorities.SERVICE_LOAD_WAHLVORSCHLAEGELISTE);

      Assertions.assertThatException()
          .isThrownBy(
              () ->
                  wahlvorschlagService.getWahlvorschlaegeListeForWahltagAndWahlID(
                      LocalDate.of(2024, 10, 10), "wahlID"))
          .isInstanceOf(NoSearchResultFoundException.class);
    }

    @Test
    void should_throwAccessDeniedException_when_anyAuthorityMissing() {
      SecurityUtils.runWith();

      Assertions.assertThatThrownBy(
              () ->
                  wahlvorschlagService.getWahlvorschlaegeListeForWahltagAndWahlID(
                      LocalDate.of(2024, 10, 10), "wahlID"))
          .isInstanceOf(AccessDeniedException.class);
    }
  }

  @Nested
  class GetReferendumvorlagenForWahlAndWahlbezirk {

    @Test
    void should_notThrowException_when_givenAllAuthorities() {
      SecurityUtils.runWith(Authorities.SERVICE_LOAD_REFERENDUMVORLAGEN);

      Assertions.assertThatException()
          .isThrownBy(
              () ->
                  wahlvorschlagService.getReferendumvorlagenForWahlAndWahlbezirk(
                      "wahlID", "wahlbezirkID"))
          .isInstanceOf(NoSearchResultFoundException.class);
    }

    @Test
    void should_throwAccessDeniedException_when_anyAuthorityMissing() {
      SecurityUtils.runWith();

      Assertions.assertThatThrownBy(
              () ->
                  wahlvorschlagService.getReferendumvorlagenForWahlAndWahlbezirk(
                      "wahlID", "wahlbezirkID"))
          .isInstanceOf(AccessDeniedException.class);
    }
  }
}
