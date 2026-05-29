package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import static org.mockito.ArgumentMatchers.any;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.status.Status;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.status.StatusRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelumschlaege.Stimmzettelumschlaege;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelumschlaege.StimmzettelumschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.authentication.AuthenticationService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Testdaten;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDateTime;
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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({TestConstants.SPRING_TEST_PROFILE, Profiles.DUMMY_CLIENTS})
class ErgebnismeldungServiceSecurityTest {

  @MockitoBean BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

  @MockitoBean AuthenticationService authenticationService;

  @Autowired ErgebnismeldungService unitUnderTest;

  @Autowired StatusRepository statusRepository;

  @Autowired StimmzettelumschlaegeRepository stimmzettelumschlaegeRepository;

  @AfterEach
  void teardown() {
    SecurityUtils.runWith(
        Authorities.REPOSITORY_DELETE_STATUS, Authorities.REPOSITORY_DELETE_STIMMZETTELUMSCHLAEGE);
    statusRepository.deleteAll();
    stimmzettelumschlaegeRepository.deleteAll();
  }

  @Nested
  class UpdateSendungszeiten {

    @Nested
    class ForBwbUser {

      @Test
      void should_notThrowException_when_allRequiredAuthoritiesAreGiven() {
        val bezirkUndWahlID = new BezirkUndWahlID("wahlID", "wahlbezirkID");

        SecurityUtils.runWith(
            Authorities.REPOSITORY_WRITE_STATUS,
            Authorities.REPOSITORY_WRITE_STIMMZETTELUMSCHLAEGE);
        statusRepository.save(
            new Status(
                bezirkUndWahlID,
                Testdaten.Meldung.createEntity(),
                Testdaten.Meldung.createEntity()));
        stimmzettelumschlaegeRepository.save(
            new Stimmzettelumschlaege(bezirkUndWahlID, LocalDateTime.now(), 0L, 0L));

        Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(any(), any()))
            .thenReturn(true);
        Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow())
            .thenReturn(WahlbezirkArtModel.BWB);

        SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_UPDATESENDUNGSZEITEN_BWB);
        Assertions.assertThatNoException()
            .isThrownBy(() -> unitUnderTest.updateSendungszeiten(bezirkUndWahlID));
      }

      @ParameterizedTest(name = "{index} - {1} missing")
      @MethodSource("getMissingAuthoritiesVariations")
      void should_throwException_when_anyRequiredAuthorityIsMissing(
          final ArgumentsAccessor argumentsAccessor) {
        try {
          val bezirkUndWahlID = new BezirkUndWahlID("wahlID", "wahlbezirkID");

          SecurityUtils.runWith(
              Authorities.REPOSITORY_WRITE_STATUS,
              Authorities.REPOSITORY_WRITE_STIMMZETTELUMSCHLAEGE);
          statusRepository.save(
              new Status(
                  bezirkUndWahlID,
                  Testdaten.Meldung.createEntity(),
                  Testdaten.Meldung.createEntity()));
          stimmzettelumschlaegeRepository.save(
              new Stimmzettelumschlaege(bezirkUndWahlID, LocalDateTime.now(), 0L, 0L));

          Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(any(), any()))
              .thenReturn(true);
          Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow())
              .thenReturn(WahlbezirkArtModel.BWB);

          SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));
          Assertions.assertThatException()
              .isThrownBy(() -> unitUnderTest.updateSendungszeiten(bezirkUndWahlID));
        } finally {
          teardown();
        }
      }

      private static Stream<Arguments> getMissingAuthoritiesVariations() {
        return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(
            ArrayUtils.removeElements(
                Authorities.ALL_AUTHORITIES_UPDATESENDUNGSZEITEN_BWB,
                // Except authorities that were caught during processing
                Authorities.SERVICE_GET_STIMMZETTELUMSCHLAEGE,
                Authorities.REPOSITORY_READ_STIMMZETTELUMSCHLAEGE));
      }
    }
  }

  @Nested
  class SendErgebnisse {}
}
