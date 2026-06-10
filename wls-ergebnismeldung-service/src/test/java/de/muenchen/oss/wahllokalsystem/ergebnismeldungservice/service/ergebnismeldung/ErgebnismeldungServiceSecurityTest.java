package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import static org.mockito.ArgumentMatchers.any;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerte;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerteRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.BezirkUndWahlIDStapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.Ergebnis;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.Ergebnisse;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.ErgebnisseRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.status.Status;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.status.StatusRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.BezirkUndWahlIDUndWaehlerverzeichnisnummer;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmabgabevermerke;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.StimmabgabevermerkeRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelumschlaege.Stimmzettelumschlaege;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelumschlaege.StimmzettelumschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.wahlscheine.Wahlscheine;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.wahlscheine.WahlscheineRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.authentication.AuthenticationService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Testdaten;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
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

  @Autowired AWerteRepository aWerteRepository;

  @Autowired ErgebnisseRepository ergebnisseRepository;

  @Autowired StimmabgabevermerkeRepository stimmabgabevermerkeRepository;

  @Autowired WahlscheineRepository wahlscheineRepository;

  @AfterEach
  void teardown() {
    SecurityUtils.runWith(
        Authorities.REPOSITORY_DELETE_STATUS,
        Authorities.REPOSITORY_DELETE_STIMMZETTELUMSCHLAEGE,
        Authorities.REPOSITORY_DELETE_STIMMZETTELUMSCHLAEGE,
        Authorities.REPOSITORY_DELETE_AWERTE,
        Authorities.REPOSITORY_DELETE_ERGEBNISSE,
        Authorities.REPOSITORY_DELETE_STIMMABGABEVERMERKE,
        Authorities.REPOSITORY_DELETE_WAHLSCHEINE);
    statusRepository.deleteAll();
    stimmzettelumschlaegeRepository.deleteAll();
    aWerteRepository.deleteAll();
    ergebnisseRepository.deleteAll();
    stimmabgabevermerkeRepository.deleteAll();
    wahlscheineRepository.deleteAll();
  }

  @Nested
  class UpdateSendungszeiten {

    @Nested
    class ForBwbUser {

      private static final WahlbezirkArtModel WAHLBEZIRKART = WahlbezirkArtModel.BWB;

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
            .thenReturn(WAHLBEZIRKART);

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
              .thenReturn(WAHLBEZIRKART);

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

    @Nested
    class ForUwbUser {

      private static final WahlbezirkArtModel WAHLBEZIRKART = WahlbezirkArtModel.UWB;

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
            .thenReturn(WAHLBEZIRKART);

        SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_UPDATESENDUNGSZEITEN_UWB);
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
              .thenReturn(WAHLBEZIRKART);

          SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));
          Assertions.assertThatException()
              .isThrownBy(() -> unitUnderTest.updateSendungszeiten(bezirkUndWahlID));
        } finally {
          teardown();
        }
      }

      private static Stream<Arguments> getMissingAuthoritiesVariations() {
        return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(
            ArrayUtils.removeElements(Authorities.ALL_AUTHORITIES_UPDATESENDUNGSZEITEN_UWB));
      }
    }
  }

  @Nested
  class SendErgebnisse {

    @Nested
    class ForBwbUser {

      private static final WahlbezirkArtModel WAHLBEZIRKART = WahlbezirkArtModel.BWB;

      @Test
      void should_throwNoException_when_allRequiredAuthoritiesAreGiven() {
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

        SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_ERGEBNISSE);
        ergebnisseRepository.save(
            new Ergebnisse(
                new BezirkUndWahlIDStapelart(
                    bezirkUndWahlID.getWahlbezirkID(),
                    bezirkUndWahlID.getWahlID(),
                    Stapelart.EUW_A),
                List.of(new Ergebnis("wahlvorschlagID", "kandidatID", 0L, 0L, 0L))));
        ergebnisseRepository.save(
            new Ergebnisse(
                new BezirkUndWahlIDStapelart(
                    bezirkUndWahlID.getWahlbezirkID(),
                    bezirkUndWahlID.getWahlID(),
                    Stapelart.EUW_B_LEER),
                List.of(new Ergebnis("wahlvorschlagID", "kandidatID", 0L, 0L, 0L))));
        ergebnisseRepository.save(
            new Ergebnisse(
                new BezirkUndWahlIDStapelart(
                    bezirkUndWahlID.getWahlbezirkID(),
                    bezirkUndWahlID.getWahlID(),
                    Stapelart.EUW_B_UNGEKENNZEICHNET),
                List.of(new Ergebnis("wahlvorschlagID", "kandidatID", 0L, 0L, 0L))));
        ergebnisseRepository.save(
            new Ergebnisse(
                new BezirkUndWahlIDStapelart(
                    bezirkUndWahlID.getWahlbezirkID(),
                    bezirkUndWahlID.getWahlID(),
                    Stapelart.EUW_C_GUELTIG),
                List.of(new Ergebnis("wahlvorschlagID", "kandidatID", 0L, 0L, 0L))));
        ergebnisseRepository.save(
            new Ergebnisse(
                new BezirkUndWahlIDStapelart(
                    bezirkUndWahlID.getWahlbezirkID(),
                    bezirkUndWahlID.getWahlID(),
                    Stapelart.EUW_C_UNGUELTIG),
                List.of(new Ergebnis("wahlvorschlagID", "kandidatID", 0L, 0L, 0L))));

        SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_WAHLSCHEINE);
        wahlscheineRepository.save(new Wahlscheine(bezirkUndWahlID, 0L));

        Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(any(), any()))
            .thenReturn(true);
        Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow())
            .thenReturn(WAHLBEZIRKART);

        SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_SEND_ERGEBNISSE_BWB);
        val sendErgebnisseModel =
            new ErgebnisseToSendCriteriaModel(
                bezirkUndWahlID.getWahlID(),
                bezirkUndWahlID.getWahlbezirkID(),
                0L,
                MeldungsartModel.V1,
                bezirkUndWahlID.getWahlbezirkID());
        Assertions.assertThatNoException()
            .isThrownBy(() -> unitUnderTest.sendErgebnisse(sendErgebnisseModel));
      }

      @ParameterizedTest(name = "{index} - {1} missing")
      @MethodSource("getMissingAuthoritiesVariations")
      void should_throwException_when_anyRequiredAuthoritiesIsMissing(
          final ArgumentsAccessor argumentsAccessor) {
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

        SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_ERGEBNISSE);
        ergebnisseRepository.save(
            new Ergebnisse(
                new BezirkUndWahlIDStapelart(
                    bezirkUndWahlID.getWahlbezirkID(),
                    bezirkUndWahlID.getWahlID(),
                    Stapelart.EUW_A),
                List.of(new Ergebnis("wahlvorschlagID", "kandidatID", 0L, 0L, 0L))));
        ergebnisseRepository.save(
            new Ergebnisse(
                new BezirkUndWahlIDStapelart(
                    bezirkUndWahlID.getWahlbezirkID(),
                    bezirkUndWahlID.getWahlID(),
                    Stapelart.EUW_B_LEER),
                List.of(new Ergebnis("wahlvorschlagID", "kandidatID", 0L, 0L, 0L))));
        ergebnisseRepository.save(
            new Ergebnisse(
                new BezirkUndWahlIDStapelart(
                    bezirkUndWahlID.getWahlbezirkID(),
                    bezirkUndWahlID.getWahlID(),
                    Stapelart.EUW_B_UNGEKENNZEICHNET),
                List.of(new Ergebnis("wahlvorschlagID", "kandidatID", 0L, 0L, 0L))));
        ergebnisseRepository.save(
            new Ergebnisse(
                new BezirkUndWahlIDStapelart(
                    bezirkUndWahlID.getWahlbezirkID(),
                    bezirkUndWahlID.getWahlID(),
                    Stapelart.EUW_C_GUELTIG),
                List.of(new Ergebnis("wahlvorschlagID", "kandidatID", 0L, 0L, 0L))));
        ergebnisseRepository.save(
            new Ergebnisse(
                new BezirkUndWahlIDStapelart(
                    bezirkUndWahlID.getWahlbezirkID(),
                    bezirkUndWahlID.getWahlID(),
                    Stapelart.EUW_C_UNGUELTIG),
                List.of(new Ergebnis("wahlvorschlagID", "kandidatID", 0L, 0L, 0L))));

        SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_WAHLSCHEINE);
        wahlscheineRepository.save(new Wahlscheine(bezirkUndWahlID, 0L));

        Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(any(), any()))
            .thenReturn(true);
        Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow())
            .thenReturn(WAHLBEZIRKART);

        SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));
        val sendErgebnisseModel =
            new ErgebnisseToSendCriteriaModel(
                bezirkUndWahlID.getWahlID(),
                bezirkUndWahlID.getWahlbezirkID(),
                0L,
                MeldungsartModel.V1,
                bezirkUndWahlID.getWahlbezirkID());
        Assertions.assertThatException()
            .isThrownBy(() -> unitUnderTest.sendErgebnisse(sendErgebnisseModel));
      }

      private static Stream<Arguments> getMissingAuthoritiesVariations() {
        return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(
            ArrayUtils.removeElements(
                Authorities.ALL_AUTHORITIES_SEND_ERGEBNISSE_BWB,
                // Except authorities that were caught during processing
                Authorities.SERVICE_GET_STIMMZETTELUMSCHLAEGE,
                Authorities.REPOSITORY_READ_STIMMZETTELUMSCHLAEGE));
      }
    }

    @Nested
    class ForUwbUser {

      private static final WahlbezirkArtModel WAHLBEZIRKART = WahlbezirkArtModel.UWB;

      @Test
      void should_throwNoException_when_allRequiredAuthoritiesAreGiven() {
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

        SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_ERGEBNISSE);
        ergebnisseRepository.save(
            new Ergebnisse(
                new BezirkUndWahlIDStapelart(
                    bezirkUndWahlID.getWahlbezirkID(),
                    bezirkUndWahlID.getWahlID(),
                    Stapelart.EUW_A),
                List.of(new Ergebnis("wahlvorschlagID", "kandidatID", 0L, 0L, 0L))));
        ergebnisseRepository.save(
            new Ergebnisse(
                new BezirkUndWahlIDStapelart(
                    bezirkUndWahlID.getWahlbezirkID(),
                    bezirkUndWahlID.getWahlID(),
                    Stapelart.EUW_B_LEER),
                List.of(new Ergebnis("wahlvorschlagID", "kandidatID", 0L, 0L, 0L))));
        ergebnisseRepository.save(
            new Ergebnisse(
                new BezirkUndWahlIDStapelart(
                    bezirkUndWahlID.getWahlbezirkID(),
                    bezirkUndWahlID.getWahlID(),
                    Stapelart.EUW_B_UNGEKENNZEICHNET),
                List.of(new Ergebnis("wahlvorschlagID", "kandidatID", 0L, 0L, 0L))));
        ergebnisseRepository.save(
            new Ergebnisse(
                new BezirkUndWahlIDStapelart(
                    bezirkUndWahlID.getWahlbezirkID(),
                    bezirkUndWahlID.getWahlID(),
                    Stapelart.EUW_C_GUELTIG),
                List.of(new Ergebnis("wahlvorschlagID", "kandidatID", 0L, 0L, 0L))));
        ergebnisseRepository.save(
            new Ergebnisse(
                new BezirkUndWahlIDStapelart(
                    bezirkUndWahlID.getWahlbezirkID(),
                    bezirkUndWahlID.getWahlID(),
                    Stapelart.EUW_C_UNGUELTIG),
                List.of(new Ergebnis("wahlvorschlagID", "kandidatID", 0L, 0L, 0L))));

        SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_AWERTE);
        aWerteRepository.save(new AWerte(bezirkUndWahlID, 0L, 0L));

        SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_STIMMABGABEVERMERKE);
        val vermerk = Testdaten.Vermerk.createEntity(0L);
        val eingenommeneWahlscheine = Testdaten.EigenommenerWahlschein.createEntity(0L);
        val stimmabgabevermerk =
            new Stimmabgabevermerke(
                new BezirkUndWahlIDUndWaehlerverzeichnisnummer(
                    bezirkUndWahlID.getWahlbezirkID(), bezirkUndWahlID.getWahlID(), 0L),
                Set.of(vermerk),
                Set.of(eingenommeneWahlscheine));
        vermerk.setStimmabgabevermerke(stimmabgabevermerk);
        stimmabgabevermerkeRepository.save(stimmabgabevermerk);

        Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(any(), any()))
            .thenReturn(true);
        Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow())
            .thenReturn(WAHLBEZIRKART);

        SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_SEND_ERGEBNISSE_UWB);
        val sendErgebnisseModel =
            new ErgebnisseToSendCriteriaModel(
                bezirkUndWahlID.getWahlID(),
                bezirkUndWahlID.getWahlbezirkID(),
                0L,
                MeldungsartModel.V1,
                bezirkUndWahlID.getWahlbezirkID());
        Assertions.assertThatNoException()
            .isThrownBy(() -> unitUnderTest.sendErgebnisse(sendErgebnisseModel));
      }

      @ParameterizedTest(name = "{index} - {1} missing")
      @MethodSource("getMissingAuthoritiesVariations")
      void should_throwException_when_anyRequiredAuthoritiesIsMissing(
          final ArgumentsAccessor argumentsAccessor) {
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

        SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_ERGEBNISSE);
        ergebnisseRepository.save(
            new Ergebnisse(
                new BezirkUndWahlIDStapelart(
                    bezirkUndWahlID.getWahlbezirkID(),
                    bezirkUndWahlID.getWahlID(),
                    Stapelart.EUW_A),
                List.of(new Ergebnis("wahlvorschlagID", "kandidatID", 0L, 0L, 0L))));
        ergebnisseRepository.save(
            new Ergebnisse(
                new BezirkUndWahlIDStapelart(
                    bezirkUndWahlID.getWahlbezirkID(),
                    bezirkUndWahlID.getWahlID(),
                    Stapelart.EUW_B_LEER),
                List.of(new Ergebnis("wahlvorschlagID", "kandidatID", 0L, 0L, 0L))));
        ergebnisseRepository.save(
            new Ergebnisse(
                new BezirkUndWahlIDStapelart(
                    bezirkUndWahlID.getWahlbezirkID(),
                    bezirkUndWahlID.getWahlID(),
                    Stapelart.EUW_B_UNGEKENNZEICHNET),
                List.of(new Ergebnis("wahlvorschlagID", "kandidatID", 0L, 0L, 0L))));
        ergebnisseRepository.save(
            new Ergebnisse(
                new BezirkUndWahlIDStapelart(
                    bezirkUndWahlID.getWahlbezirkID(),
                    bezirkUndWahlID.getWahlID(),
                    Stapelart.EUW_C_GUELTIG),
                List.of(new Ergebnis("wahlvorschlagID", "kandidatID", 0L, 0L, 0L))));
        ergebnisseRepository.save(
            new Ergebnisse(
                new BezirkUndWahlIDStapelart(
                    bezirkUndWahlID.getWahlbezirkID(),
                    bezirkUndWahlID.getWahlID(),
                    Stapelart.EUW_C_UNGUELTIG),
                List.of(new Ergebnis("wahlvorschlagID", "kandidatID", 0L, 0L, 0L))));

        SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_AWERTE);
        aWerteRepository.save(new AWerte(bezirkUndWahlID, 0L, 0L));

        SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_STIMMABGABEVERMERKE);
        val vermerk = Testdaten.Vermerk.createEntity(0L);
        val eingenommeneWahlscheine = Testdaten.EigenommenerWahlschein.createEntity(0L);
        val stimmabgabevermerk =
            new Stimmabgabevermerke(
                new BezirkUndWahlIDUndWaehlerverzeichnisnummer(
                    bezirkUndWahlID.getWahlbezirkID(), bezirkUndWahlID.getWahlID(), 0L),
                Set.of(vermerk),
                Set.of(eingenommeneWahlscheine));
        vermerk.setStimmabgabevermerke(stimmabgabevermerk);
        stimmabgabevermerkeRepository.save(stimmabgabevermerk);

        Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(any(), any()))
            .thenReturn(true);
        Mockito.when(authenticationService.getWahlbezirkArtOfCurrentAuthenticationOrThrow())
            .thenReturn(WAHLBEZIRKART);

        SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));
        val sendErgebnisseModel =
            new ErgebnisseToSendCriteriaModel(
                bezirkUndWahlID.getWahlID(),
                bezirkUndWahlID.getWahlbezirkID(),
                0L,
                MeldungsartModel.V1,
                bezirkUndWahlID.getWahlbezirkID());
        Assertions.assertThatException()
            .isThrownBy(() -> unitUnderTest.sendErgebnisse(sendErgebnisseModel));
      }

      private static Stream<Arguments> getMissingAuthoritiesVariations() {
        return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(
            ArrayUtils.removeElements(
                Authorities.ALL_AUTHORITIES_SEND_ERGEBNISSE_UWB,
                // Except authorities that were caught during processing
                Authorities.SERVICE_GET_STIMMZETTELUMSCHLAEGE,
                Authorities.REPOSITORY_READ_STIMMZETTELUMSCHLAEGE));
      }
    }
  }
}
