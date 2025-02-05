package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.validation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerte;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerteRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.BezirkUndWahlIDStapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.Ergebnisse;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.ErgebnisseRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmabgabevermerke;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.StimmabgabevermerkeRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.wahlscheine.Wahlscheine;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.wahlscheine.WahlscheineRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AWerteService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import java.util.Optional;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultElectionTypeValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    AWerteRepository aWerteRepo;

    @Mock
    ErgebnisseRepository ergebnisseRepo;

    @Mock
    StimmabgabevermerkeRepository stimmabgabevermerkeRepo;

    @Mock
    WahlscheineRepository wahlscheineRepo;

    @Mock
    AWerteService aWerteService;

    @InjectMocks
    DefaultElectionTypeValidator unitUnderTest;

    @Nested
    class CheckValidation {

        @Nested
        class WithWahlbezirkArtBWB {

            final WahlbezirkArtModel wahlbezirkArt = WahlbezirkArtModel.BWB;

            @Test
            void should_returnTrue_when_stapelAndWahlscheineAreValid() {
                val wahlbezirkID = "wahlbezirkID";
                val wahlID = "wahlID";
                val waehlerverzeichnisNummer = 0L;
                val requiredStapel = List.of(Stapelart.BTW_A, Stapelart.BTW_B_I_GUELTIG);

                val mockedRepoErgebnisse = List.of(createErgebnisWithStapelArt(Stapelart.BTW_A), createErgebnisWithStapelArt(Stapelart.BTW_B_I_GUELTIG));
                val mockedRepoWahlscheine = new Wahlscheine();

                Mockito.when(ergebnisseRepo.findByWahlbezirkIDAndWahlD(eq(wahlbezirkID), eq(wahlID))).thenReturn(mockedRepoErgebnisse);
                Mockito.when(wahlscheineRepo.findById(new BezirkUndWahlID(wahlID, wahlbezirkID))).thenReturn(Optional.of(mockedRepoWahlscheine));

                val result = unitUnderTest.checkValidation(wahlbezirkArt, wahlbezirkID, wahlID, waehlerverzeichnisNummer, requiredStapel);

                Assertions.assertThat(result).isTrue();
            }

            @Test
            void should_throwFachlicheWlsException_when_requiredStapelArtIsMissing() {
                val wahlbezirkID = "wahlbezirkID";
                val wahlID = "wahlID";
                val waehlerverzeichnisNummer = 0L;
                val requiredStapel = List.of(Stapelart.BTW_A, Stapelart.BTW_B_I_GUELTIG);

                val mockedRepoErgebnisse = List.of(createErgebnisWithStapelArt(Stapelart.BTW_A));
                val mockedWlsException = FachlicheWlsException.withCode("000").buildWithMessage("Required stapel art is missing");

                Mockito.when(ergebnisseRepo.findByWahlbezirkIDAndWahlD(eq(wahlbezirkID), eq(wahlID))).thenReturn(mockedRepoErgebnisse);
                Mockito.when(exceptionFactory.createFachlicheWlsException(any(ExceptionDataWrapper.class)))
                        .thenReturn(mockedWlsException);

                Assertions.assertThatThrownBy(
                                () -> unitUnderTest.checkValidation(wahlbezirkArt, wahlbezirkID, wahlID, waehlerverzeichnisNummer, requiredStapel))
                        .isSameAs(mockedWlsException);
            }

            @Test
            void should_throwFachlicheWlsException_when_noWahlscheineExists() {
                val wahlbezirkID = "wahlbezirkID";
                val wahlID = "wahlID";
                val waehlerverzeichnisNummer = 0L;
                val requiredStapel = List.of(Stapelart.BTW_A, Stapelart.BTW_B_I_GUELTIG);

                val mockedRepoErgebnisse = List.of(createErgebnisWithStapelArt(Stapelart.BTW_A), createErgebnisWithStapelArt(Stapelart.BTW_B_I_GUELTIG));
                val mockedWlsException = FachlicheWlsException.withCode("000").buildWithMessage("Required wahlscheine are missing");

                Mockito.when(ergebnisseRepo.findByWahlbezirkIDAndWahlD(eq(wahlbezirkID), eq(wahlID))).thenReturn(mockedRepoErgebnisse);
                Mockito.when(wahlscheineRepo.findById(new BezirkUndWahlID(wahlID, wahlbezirkID))).thenReturn(Optional.empty());
                Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SENDERGEBNISSE_WAHLSCHEINE_UNVOLLSTAENDIG))
                        .thenReturn(mockedWlsException);

                Assertions.assertThatThrownBy(
                                () -> unitUnderTest.checkValidation(wahlbezirkArt, wahlbezirkID, wahlID, waehlerverzeichnisNummer, requiredStapel))
                        .isSameAs(mockedWlsException);
            }
        }

        @Nested
        class WithWahlbezirkArtUWB {

            final WahlbezirkArtModel wahlbezirkArt = WahlbezirkArtModel.UWB;

            @Test
            void should_returnTrue_when_stapelStimmabgabevermerkeAndAWerteAreValid() {
                val wahlbezirkID = "wahlbezirkID";
                val wahlID = "wahlID";
                val waehlerverzeichnisNummer = 0L;
                val requiredStapel = List.of(Stapelart.BTW_A, Stapelart.BTW_B_I_GUELTIG);

                val mockedRepoErgebnisse = List.of(createErgebnisWithStapelArt(Stapelart.BTW_A), createErgebnisWithStapelArt(Stapelart.BTW_B_I_GUELTIG));
                val mockedStimmabgabevermerke = new Stimmabgabevermerke();
                val mockedAWerte = new AWerte();

                Mockito.when(ergebnisseRepo.findByWahlbezirkIDAndWahlD(eq(wahlbezirkID), eq(wahlID))).thenReturn(mockedRepoErgebnisse);
                Mockito.when(stimmabgabevermerkeRepo.findById(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer)))
                        .thenReturn(Optional.of(mockedStimmabgabevermerke));
                Mockito.when(aWerteRepo.findById(new BezirkUndWahlID(wahlID, wahlbezirkID))).thenReturn(Optional.of(mockedAWerte));

                val result = unitUnderTest.checkValidation(wahlbezirkArt, wahlbezirkID, wahlID, waehlerverzeichnisNummer, requiredStapel);

                Assertions.assertThat(result).isTrue();
            }

            @Test
            void should_throwFachlicheWlsException_when_requiredStapelIsMissing() {
                val wahlbezirkID = "wahlbezirkID";
                val wahlID = "wahlID";
                val waehlerverzeichnisNummer = 0L;
                val requiredStapel = List.of(Stapelart.BTW_A, Stapelart.BTW_B_I_GUELTIG);

                val mockedRepoErgebnisse = List.of(createErgebnisWithStapelArt(Stapelart.BTW_A));
                val mockedWlsException = FachlicheWlsException.withCode("000").buildWithMessage("Required stapel art is missing");

                Mockito.when(ergebnisseRepo.findByWahlbezirkIDAndWahlD(eq(wahlbezirkID), eq(wahlID))).thenReturn(mockedRepoErgebnisse);
                Mockito.when(exceptionFactory.createFachlicheWlsException(any(ExceptionDataWrapper.class)))
                        .thenReturn(mockedWlsException);

                Assertions.assertThatThrownBy(
                                () -> unitUnderTest.checkValidation(wahlbezirkArt, wahlbezirkID, wahlID, waehlerverzeichnisNummer, requiredStapel))
                        .isSameAs(mockedWlsException);
            }

            @Test
            void should_throwFachlicheWlsException_when_noStimmabgabevermerkeExists() {
                val wahlbezirkID = "wahlbezirkID";
                val wahlID = "wahlID";
                val waehlerverzeichnisNummer = 0L;
                val requiredStapel = List.of(Stapelart.BTW_A, Stapelart.BTW_B_I_GUELTIG);

                val mockedRepoErgebnisse = List.of(createErgebnisWithStapelArt(Stapelart.BTW_A), createErgebnisWithStapelArt(Stapelart.BTW_B_I_GUELTIG));
                val mockedWlsException = FachlicheWlsException.withCode("000").buildWithMessage("Required stimmabgabevermerke are missing");

                Mockito.when(ergebnisseRepo.findByWahlbezirkIDAndWahlD(eq(wahlbezirkID), eq(wahlID))).thenReturn(mockedRepoErgebnisse);
                Mockito.when(stimmabgabevermerkeRepo.findById(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer)))
                        .thenReturn(Optional.empty());
                Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SENDERGEBNISSE_STIMMABGABEVERMERKE_UNVOLLSTAENDIG))
                        .thenReturn(mockedWlsException);

                Assertions.assertThatThrownBy(
                                () -> unitUnderTest.checkValidation(wahlbezirkArt, wahlbezirkID, wahlID, waehlerverzeichnisNummer, requiredStapel))
                        .isSameAs(mockedWlsException);
            }

            @Test
            void should_throwFachlicheWlsException_when_noAWerteExists() {
                val wahlbezirkID = "wahlbezirkID";
                val wahlID = "wahlID";
                val waehlerverzeichnisNummer = 0L;
                val requiredStapel = List.of(Stapelart.BTW_A, Stapelart.BTW_B_I_GUELTIG);

                val mockedRepoErgebnisse = List.of(createErgebnisWithStapelArt(Stapelart.BTW_A), createErgebnisWithStapelArt(Stapelart.BTW_B_I_GUELTIG));
                val mockedStimmabgabevermerke = new Stimmabgabevermerke();
                val mockedWlsException = FachlicheWlsException.withCode("000").buildWithMessage("Required A-Werte are missing");

                Mockito.when(ergebnisseRepo.findByWahlbezirkIDAndWahlD(eq(wahlbezirkID), eq(wahlID))).thenReturn(mockedRepoErgebnisse);
                Mockito.when(stimmabgabevermerkeRepo.findById(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer)))
                        .thenReturn(Optional.of(mockedStimmabgabevermerke));
                Mockito.when(aWerteRepo.findById(new BezirkUndWahlID(wahlID, wahlbezirkID))).thenReturn(Optional.empty());
                Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SENDERGEBNISSE_AWERTE_UNVOLLSTAENDIG))
                        .thenReturn(mockedWlsException);

                Assertions.assertThatThrownBy(
                                () -> unitUnderTest.checkValidation(wahlbezirkArt, wahlbezirkID, wahlID, waehlerverzeichnisNummer, requiredStapel))
                        .isSameAs(mockedWlsException);
            }

            @Test
            void should_returnTrue_when_aWerteExistsAfterLoadingThem() {
                val wahlbezirkID = "wahlbezirkID";
                val wahlID = "wahlID";
                val waehlerverzeichnisNummer = 0L;
                val requiredStapel = List.of(Stapelart.BTW_A, Stapelart.BTW_B_I_GUELTIG);

                val mockedRepoErgebnisse = List.of(createErgebnisWithStapelArt(Stapelart.BTW_A), createErgebnisWithStapelArt(Stapelart.BTW_B_I_GUELTIG));
                val mockedStimmabgabevermerke = new Stimmabgabevermerke();
                val mockedAWerte = new AWerte();
                final Optional<AWerte> mockedNotFound = Optional.empty();

                Mockito.when(ergebnisseRepo.findByWahlbezirkIDAndWahlD(eq(wahlbezirkID), eq(wahlID))).thenReturn(mockedRepoErgebnisse);
                Mockito.when(stimmabgabevermerkeRepo.findById(new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer)))
                        .thenReturn(Optional.of(mockedStimmabgabevermerke));
                Mockito.when(aWerteRepo.findById(new BezirkUndWahlID(wahlID, wahlbezirkID)))
                        .thenReturn(mockedNotFound, Optional.of(mockedAWerte));

                val result = unitUnderTest.checkValidation(wahlbezirkArt, wahlbezirkID, wahlID, waehlerverzeichnisNummer, requiredStapel);

                Assertions.assertThat(result).isTrue();

                Mockito.verify(aWerteService).getAWerte(wahlbezirkID);
            }

        }

        private Ergebnisse createErgebnisWithStapelArt(final Stapelart stapelart) {
            val ergebnis = new Ergebnisse();

            ergebnis.setBezirkUndWahlIDStapelart(new BezirkUndWahlIDStapelart("wahlbezirkID", "wahlID", stapelart));

            return ergebnis;
        }
    }
}
