package de.muenchen.oss.wahllokalsystem.basisdatenservice.service.nachlieferungsbezirke;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common.WahltagIdUndWahlbezirkId;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.nachlieferungsbezirke.Nachlieferungsbezirk;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.nachlieferungsbezirke.NachlieferungsbezirkeRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.List;
import java.util.stream.Stream;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({TestConstants.SPRING_TEST_PROFILE})
public class NachlieferungsbezirkeServiceSecurityTest {

  @Autowired NachlieferungsbezirkeService unitUnderTest;

  @Autowired NachlieferungsbezirkeRepository nachlieferungsbezirkeRepository;

  @MockitoBean BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

  @AfterEach
  void teardown() {
    SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_NACHLIEFERUNGSBEZIRKE);
    nachlieferungsbezirkeRepository.deleteAll();
  }

  @Nested
  class CheckForNachlieferungsbezirke {

    @Test
    void should_grantAccess_when_authoritiesArePresent() {
      val wahltagID = "wahltagID";
      val wahlbezirkID = "wahlbezirkID";

      SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_CHECK_NACHLIEFERUNGSBEZIRKE);
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(wahlbezirkID), Mockito.any()))
          .thenReturn(true);

      Assertions.assertThatNoException()
          .isThrownBy(() -> unitUnderTest.checkForNachlieferungsbezirk(wahltagID, wahlbezirkID));
    }

    @ParameterizedTest(name = "{index} - {1} missing")
    @MethodSource("getMissingAuthoritiesVariations")
    void should_denyAccess_when_anyAuthorityIsMissing(final ArgumentsAccessor argumentsAccessor) {
      val wahltagID = "wahltagID";
      val wahlbezirkID = "wahlbezirkID";

      SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(wahlbezirkID), Mockito.any()))
          .thenReturn(true);

      Assertions.assertThatThrownBy(
              () -> unitUnderTest.checkForNachlieferungsbezirk(wahltagID, wahlbezirkID))
          .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void should_throwAccessDeniedException_when_bezirkIDPermissionEvaluatorReturnsFalse() {
      val wahltagID = "wahltagID";
      val wahlbezirkID = "wahlbezirkID";

      SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_CHECK_NACHLIEFERUNGSBEZIRKE);
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(wahlbezirkID), Mockito.any()))
          .thenReturn(false);

      Assertions.assertThatThrownBy(
              () -> unitUnderTest.checkForNachlieferungsbezirk(wahltagID, wahlbezirkID))
          .isInstanceOf(AccessDeniedException.class);
    }

    private static Stream<Arguments> getMissingAuthoritiesVariations() {
      return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(
          Authorities.ALL_AUTHORITIES_CHECK_NACHLIEFERUNGSBEZIRKE);
    }
  }

  @Nested
  class SetNachlieferungsbezirke {

    @Test
    void should_grantAccess_when_authoritiesArePresent() {
      val wahltagID = "wahltagID";
      val nachlieferungsbezirke = List.of("wahlbezirkID");

      SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_SET_NACHLIEFERUNGSBEZIRKE);

      Assertions.assertThatNoException()
          .isThrownBy(
              () -> unitUnderTest.setNachlieferungsbezirke(wahltagID, nachlieferungsbezirke));
    }

    @ParameterizedTest(name = "{index} - {1} missing")
    @MethodSource("getMissingAuthoritiesVariations")
    void should_denyAccess_when_anyAuthorityIsMissing(final ArgumentsAccessor argumentsAccessor) {
      val wahltagID = "wahltagID";
      val wahlbezirkID = "wahlbezirkID";
      val nachlieferungsbezirke = List.of(wahlbezirkID);

      SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_NACHLIEFERUNGSBEZIRKE);
      nachlieferungsbezirkeRepository.save(
          new Nachlieferungsbezirk(new WahltagIdUndWahlbezirkId(wahltagID, wahlbezirkID)));

      SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));

      Assertions.assertThatThrownBy(
              () -> unitUnderTest.setNachlieferungsbezirke(wahltagID, nachlieferungsbezirke))
          .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void should_throwTechnischeWlsException_when_repoWriteAuthorityIsMissing() {
      val wahltagID = "wahltagID";
      val wahlbezirkID = "wahlbezirkID";
      val nachlieferungsbezirke = List.of(wahlbezirkID);

      SecurityUtils.runWith(
          Authorities.SERVICE_POST_NACHLIEFERUNGSBEZIRKE,
          Authorities.REPOSITORY_READ_NACHLIEFERUNGSBEZIRKE);
      Assertions.assertThatThrownBy(
              () -> unitUnderTest.setNachlieferungsbezirke(wahltagID, nachlieferungsbezirke))
          .isInstanceOf(TechnischeWlsException.class);
    }

    private static Stream<Arguments> getMissingAuthoritiesVariations() {
      return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(
          new String[] {
            Authorities.SERVICE_POST_NACHLIEFERUNGSBEZIRKE,
            Authorities.REPOSITORY_READ_NACHLIEFERUNGSBEZIRKE,
            Authorities.REPOSITORY_DELETE_NACHLIEFERUNGSBEZIRKE
          });
    }
  }
}
