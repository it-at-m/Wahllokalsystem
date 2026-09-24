package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.validation.electionType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.validation.DefaultElectionTypeValidator;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw.MBWBedenklicheStimmzettelService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.status.ErfassungStatusModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.status.StimmzettelerfassungService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MbwValidationImplTest {

  @Mock DefaultElectionTypeValidator defaultElectionTypeValidator;

  @Mock MBWBedenklicheStimmzettelService mbwBedenklicheStimmzettelService;

  @Mock StimmzettelerfassungService stimmzettelerfassungService;

  @InjectMocks MbwValidationImpl unitUnderTest;

  @Captor ArgumentCaptor<List<Stapelart>> captorStapelList;

  @Nested
  class SupportsWahlart {

    @Test
    void should_returnTrue_when_wahlartIsMBW() {
      Assertions.assertThat(unitUnderTest.supportsWahlart(WahlartModel.MBW)).isTrue();
    }

    @ParameterizedTest
    @MethodSource("argumentsForNonMBWWahlart")
    void should_returnFalse_when_wahlartIsNotMBW(final ArgumentsAccessor arguments) {
      Assertions.assertThat(unitUnderTest.supportsWahlart(arguments.get(0, WahlartModel.class)))
          .isFalse();
    }

    public static Stream<Arguments> argumentsForNonMBWWahlart() {
      return Arrays.stream(WahlartModel.values())
          .filter(wahlart -> !WahlartModel.MBW.equals(wahlart))
          .map(Arguments::of);
    }
  }

  @Nested
  class IsValidUwb {

    @Test
    void should_callDefaultValidatorWithBTWStapel_when_isCalled() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val waehlerverzeichnisNummer = 0L;
      val meldungsart = MeldungsartModel.V1;

      unitUnderTest.isValidUwb(wahlbezirkID, wahlID, waehlerverzeichnisNummer, meldungsart);

      Mockito.verify(defaultElectionTypeValidator)
          .checkValidation(
              eq(WahlbezirkArtModel.UWB),
              eq(wahlbezirkID),
              eq(wahlID),
              eq(waehlerverzeichnisNummer),
              captorStapelList.capture());

      val expectedStapel =
          Arrays.stream(Stapelart.values())
              .filter(stapelart -> !Stapelart.MBW_A_B.equals(stapelart))
              .filter(stapelart -> !Stapelart.MBW_D.equals(stapelart))
              .filter(stapelart -> !Stapelart.MBW_B_C.equals(stapelart))
              .filter(stapelart -> stapelart.name().startsWith("MBW_"))
              .toList()
              .toArray(new Stapelart[0]);

      Assertions.assertThat(captorStapelList.getValue()).containsExactlyInAnyOrder(expectedStapel);
    }

    @ParameterizedTest
    @MethodSource("validationParameters")
    void should_returnResponseOfDefaultValidator_when_isCalled(
        boolean mockedValidatorResponse,
        boolean mockedHasBedenklicheStimmzettel,
        boolean mockedResult) {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val waehlerverzeichnisNummer = 0L;
      val meldungsart = MeldungsartModel.V1;

      Mockito.when(
              defaultElectionTypeValidator.checkValidation(
                  eq(WahlbezirkArtModel.UWB), anyString(), anyString(), any(), any()))
          .thenReturn(mockedValidatorResponse);
      if (mockedValidatorResponse) {
        Mockito.when(mbwBedenklicheStimmzettelService.hasBedenklicheStimmzettel(any()))
            .thenReturn(mockedHasBedenklicheStimmzettel);
      }

      val result =
          unitUnderTest.isValidUwb(wahlbezirkID, wahlID, waehlerverzeichnisNummer, meldungsart);

      Assertions.assertThat(result).isEqualTo(mockedResult);
    }

    private static Stream<Arguments> validationParameters() {
      return Stream.of(
          Arguments.of(true, true, true),
          Arguments.of(true, false, false),
          Arguments.of(false, true, false),
          Arguments.of(false, false, false));
    }

    @ParameterizedTest
    @MethodSource("dseValidationParameters")
    void should_returnDseValidationResult_when_stapelValidationIsInvalid(
        final MeldungsartModel meldungsart,
        final Optional<ErfassungStatusModel> erfassungStatus,
        final boolean expectedResult) {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val waehlerverzeichnisNummer = 0L;

      Mockito.lenient()
          .when(
              defaultElectionTypeValidator.checkValidation(
                  any(), anyString(), anyString(), any(), any()))
          .thenReturn(false);
      Mockito.when(stimmzettelerfassungService.getStimmzettelerfassungStatus(any()))
          .thenReturn(erfassungStatus);

      val result =
          unitUnderTest.isValidUwb(wahlbezirkID, wahlID, waehlerverzeichnisNummer, meldungsart);

      Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> dseValidationParameters() {
      return dseValidationParametersForAllMeldungsarten();
    }

    @ParameterizedTest
    @EnumSource(MeldungsartModel.class)
    void should_notUseDefaultValidator_when_dseValidationResultIsTrue(
        final MeldungsartModel meldungsart) {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val waehlerverzeichnisNummer = 0L;

      Mockito.when(stimmzettelerfassungService.getStimmzettelerfassungStatus(any()))
          .thenReturn(Optional.of(ErfassungStatusModel.BE_ABGESCHLOSSEN));

      val result =
          unitUnderTest.isValidUwb(wahlbezirkID, wahlID, waehlerverzeichnisNummer, meldungsart);

      Assertions.assertThat(result).isEqualTo(true);
      // we have to avoid to get exception to early: #793
      Mockito.verifyNoInteractions(defaultElectionTypeValidator);
    }
  }

  @Nested
  class IsValidBwb {

    @Test
    void should_callDefaultValidatorWithBTWStapel_when_isCalled() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val waehlerverzeichnisNummer = 0L;
      val meldungsart = MeldungsartModel.V1;

      unitUnderTest.isValidBwb(wahlbezirkID, wahlID, waehlerverzeichnisNummer, meldungsart);

      Mockito.verify(defaultElectionTypeValidator)
          .checkValidation(
              eq(WahlbezirkArtModel.BWB),
              eq(wahlbezirkID),
              eq(wahlID),
              eq(waehlerverzeichnisNummer),
              captorStapelList.capture());

      val expectedStapel =
          Arrays.stream(Stapelart.values())
              .filter(stapelart -> !Stapelart.MBW_A_B.equals(stapelart))
              .filter(stapelart -> !Stapelart.MBW_D.equals(stapelart))
              .filter(stapelart -> !Stapelart.MBW_B_C.equals(stapelart))
              .filter(stapelart -> stapelart.name().startsWith("MBW_"))
              .toList()
              .toArray(new Stapelart[0]);

      Assertions.assertThat(captorStapelList.getValue()).containsExactlyInAnyOrder(expectedStapel);
    }

    @ParameterizedTest
    @MethodSource("validationParameters")
    void should_returnResponseOfDefaultValidator_when_isCalled(
        boolean mockedValidatorResponse,
        boolean mockedHasBedenklicheStimmzettel,
        boolean mockedResult) {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val waehlerverzeichnisNummer = 0L;
      val meldungsart = MeldungsartModel.V1;

      Mockito.when(
              defaultElectionTypeValidator.checkValidation(
                  eq(WahlbezirkArtModel.BWB), anyString(), anyString(), any(), any()))
          .thenReturn(mockedValidatorResponse);
      if (mockedValidatorResponse) {
        Mockito.when(mbwBedenklicheStimmzettelService.hasBedenklicheStimmzettel(any()))
            .thenReturn(mockedHasBedenklicheStimmzettel);
      }

      val result =
          unitUnderTest.isValidBwb(wahlbezirkID, wahlID, waehlerverzeichnisNummer, meldungsart);

      Assertions.assertThat(result).isEqualTo(mockedResult);
    }

    private static Stream<Arguments> validationParameters() {
      return Stream.of(
          Arguments.of(true, true, true),
          Arguments.of(true, false, false),
          Arguments.of(false, true, false),
          Arguments.of(false, false, false));
    }

    @ParameterizedTest
    @MethodSource("dseValidationParameters")
    void should_returnDseValidationResult_when_stapelValidationIsInvalid(
        final MeldungsartModel meldungsart,
        final Optional<ErfassungStatusModel> erfassungStatus,
        final boolean expectedResult) {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val waehlerverzeichnisNummer = 0L;

      Mockito.lenient()
          .when(
              defaultElectionTypeValidator.checkValidation(
                  any(), anyString(), anyString(), any(), any()))
          .thenReturn(false);
      Mockito.when(stimmzettelerfassungService.getStimmzettelerfassungStatus(any()))
          .thenReturn(erfassungStatus);

      val result =
          unitUnderTest.isValidBwb(wahlbezirkID, wahlID, waehlerverzeichnisNummer, meldungsart);

      Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> dseValidationParameters() {
      return dseValidationParametersForAllMeldungsarten();
    }

    @ParameterizedTest
    @EnumSource(MeldungsartModel.class)
    void should_notUseDefaultValidator_when_dseValidationResultIsTrue(
        final MeldungsartModel meldungsart) {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val waehlerverzeichnisNummer = 0L;

      Mockito.when(stimmzettelerfassungService.getStimmzettelerfassungStatus(any()))
          .thenReturn(Optional.of(ErfassungStatusModel.BE_ABGESCHLOSSEN));

      val result =
          unitUnderTest.isValidBwb(wahlbezirkID, wahlID, waehlerverzeichnisNummer, meldungsart);

      Assertions.assertThat(result).isEqualTo(true);
      // we have to avoid to get exception to early: #793
      Mockito.verifyNoInteractions(defaultElectionTypeValidator);
    }
  }

  private static Stream<Arguments> dseValidationParametersForAllMeldungsarten() {
    return Stream.of(
        Arguments.of(MeldungsartModel.V3, Optional.empty(), false),
        Arguments.of(MeldungsartModel.V3, Optional.of(ErfassungStatusModel.STE_BEARBEITUNG), false),
        Arguments.of(
            MeldungsartModel.V3, Optional.of(ErfassungStatusModel.STE_ABGESCHLOSSEN), true),
        Arguments.of(MeldungsartModel.V3, Optional.of(ErfassungStatusModel.BE_ABGESCHLOSSEN), true),
        Arguments.of(MeldungsartModel.V1, Optional.empty(), false),
        Arguments.of(MeldungsartModel.V1, Optional.of(ErfassungStatusModel.STE_BEARBEITUNG), false),
        Arguments.of(
            MeldungsartModel.V1, Optional.of(ErfassungStatusModel.STE_ABGESCHLOSSEN), false),
        Arguments.of(
            MeldungsartModel.V1, Optional.of(ErfassungStatusModel.BE_ABGESCHLOSSEN), true));
  }
}
