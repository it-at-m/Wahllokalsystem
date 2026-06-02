package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.wahltag.KonfigurierterWahltagRepository;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDate;
import java.util.stream.Stream;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
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
class KonfigurierterWahltagServiceSecurityTest {

  @Autowired KonfigurierterWahltagService unitUnderTest;

  @Autowired KonfigurierterWahltagRepository konfigurierterWahltagRepository;

  @AfterEach
  void teardown() {
    SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_KONFIGURIERTERWAHLTAG);
    konfigurierterWahltagRepository.deleteAll();
  }

  @Nested
  class GetKonfigurierterWahltag {

    @Test
    void should_notThrowException_when_allAuthoritiesAreGiven() {
      SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_KONFIGURIERTERWAHLTAG);
      Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getKonfigurierterWahltag());
    }

    @ParameterizedTest(name = "{index} - {1} missing")
    @MethodSource("getMissingAuthoritiesVariations")
    void should_throwAccessDeniedException_when_anyRequiredAuthorityIsMissing(
        final ArgumentsAccessor argumentsAccessor) {
      SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));
      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.getKonfigurierterWahltag())
          .isInstanceOf(AccessDeniedException.class);
    }

    private static Stream<Arguments> getMissingAuthoritiesVariations() {
      return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(
          Authorities.ALL_AUTHORITIES_GET_KONFIGURIERTERWAHLTAG);
    }
  }

  @Nested
  class SetKonfigurierterWahltag {

    @Test
    void should_notThrowException_when_allAuthoritiesAreGiven() {
      SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_SET_KONFIGURIERTERWAHLTAG);
      val konfigurierterWahltag =
          new KonfigurierterWahltagModel(LocalDate.now(), "wahltagID", false, "0");
      Assertions.assertThatNoException()
          .isThrownBy(() -> unitUnderTest.setKonfigurierterWahltag(konfigurierterWahltag));
    }

    @ParameterizedTest(name = "{index} - {1} missing")
    @MethodSource("getMissingAuthoritiesVariations")
    void should_throwAccessDeniedException_when_anyRequiredAuthorityIsMissing(
        final ArgumentsAccessor argumentsAccessor) {
      val konfigurierterWahltag =
          new KonfigurierterWahltagModel(LocalDate.now(), "wahltagID", false, "0");
      SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));
      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.setKonfigurierterWahltag(konfigurierterWahltag))
          .isInstanceOf(AccessDeniedException.class);
    }

    private static Stream<Arguments> getMissingAuthoritiesVariations() {
      return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(
          Authorities.ALL_AUTHORITIES_SET_KONFIGURIERTERWAHLTAG);
    }
  }

  @Nested
  class DeleteKonfigurierterWahltag {

    @Test
    void should_notThrowException_when_allAuthoritiesAreGiven() {
      SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_DELETE_KONFIGURIERTERWAHLTAG);
      Assertions.assertThatNoException()
          .isThrownBy(() -> unitUnderTest.deleteKonfigurierterWahltag("wahltagID"));
    }

    @Test
    void should_throwAccessDeniedException_when_serviceAuthorityIsMissing() {
      SecurityUtils.runWith(
          ArrayUtils.removeElements(
              Authorities.ALL_AUTHORITIES_DELETE_KONFIGURIERTERWAHLTAG,
              Authorities.SERVICE_DELETE_KONFIGURIERTERWAHLTAG));
      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.deleteKonfigurierterWahltag("wahltagID"))
          .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void should_throwTechnischeWlsException_when_repoAuthorityIsMissing() {
      SecurityUtils.runWith(
          ArrayUtils.removeElements(
              Authorities.ALL_AUTHORITIES_DELETE_KONFIGURIERTERWAHLTAG,
              Authorities.REPOSITORY_DELETE_KONFIGURIERTERWAHLTAG));
      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.deleteKonfigurierterWahltag("wahltagID"))
          .isInstanceOf(TechnischeWlsException.class);
    }
  }

  @Nested
  class GetKonfigurierteWahltage {

    @Test
    void should_notThrowException_when_allAuthoritiesAreGiven() {
      SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_KONFIGURIERTERWAHLTAGE);
      Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getKonfigurierteWahltage());
    }

    @ParameterizedTest(name = "{index} - {1} missing")
    @MethodSource("getMissingAuthoritiesVariations")
    void should_throwAccessDeniedException_when_anyRequiredAuthorityIsMissing(
        final ArgumentsAccessor argumentsAccessor) {
      SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));
      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.getKonfigurierteWahltage())
          .isInstanceOf(AccessDeniedException.class);
    }

    private static Stream<Arguments> getMissingAuthoritiesVariations() {
      return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(
          Authorities.ALL_AUTHORITIES_GET_KONFIGURIERTERWAHLTAGE);
    }
  }
}
