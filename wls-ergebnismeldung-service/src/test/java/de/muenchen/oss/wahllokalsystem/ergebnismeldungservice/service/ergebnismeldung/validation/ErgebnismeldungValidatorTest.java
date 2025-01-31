package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.validation;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.ErgebnisseToSendCriteriaModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlartModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import java.util.stream.Stream;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ErgebnismeldungValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    ElectionTypeValidation validator;

    @Mock
    List<ElectionTypeValidation> electionTypeValidations;

    @InjectMocks
    ErgebnismeldungValidator unitUnderTest;

    @Nested
    class ValidBezirkUndWahlIDOrThrow {

        @Test
        void should_notThrowAnyException_when_bezirkUndWahlIDIsValid() {
            val bezirkUndWahlID = new BezirkUndWahlID("wahlID", "wahlbezirkID");

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validBezirkUndWahlIDOrThrow(bezirkUndWahlID));
        }

        @ParameterizedTest(name = "{1}")
        @MethodSource("streamWithInvalidBezirkUndWahlID")
        void should_throwFachlicheWlsException_when_bezirkUndWahlIDIsInvalid(final ArgumentsAccessor arguments) {
            val mockedWlsException = FachlicheWlsException.withCode("000").buildWithMessage("validation failed");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SENDERGEBNISSE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validBezirkUndWahlIDOrThrow(arguments.get(0, BezirkUndWahlID.class)))
                    .isSameAs(mockedWlsException);
        }

        public static Stream<Arguments> streamWithInvalidBezirkUndWahlID() {
            return Stream.of(
                    Arguments.of(null, "bezirkUndWahlID is null"),
                    Arguments.of(new BezirkUndWahlID(null, "wahlbezirkID"), "wahlID is null"),
                    Arguments.of(new BezirkUndWahlID("", "wahlbezirkID"), "wahlID is empty"),
                    Arguments.of(new BezirkUndWahlID(" ", "wahlbezirkID"), "wahlID is blank"),
                    Arguments.of(new BezirkUndWahlID("wahlID", null), "wahlbezirkID is null"),
                    Arguments.of(new BezirkUndWahlID("wahlID", ""), "wahlbezirkID is empty"),
                    Arguments.of(new BezirkUndWahlID("wahlID", "   "), "wahlbezirkID is blank"));
        }
    }

    @Nested
    class ValidErgebnisseToSendCriteriaOrThrow {

        @Test
        void should_notThrowAnyException_when_ergebnisseToSendCriteriaAreValid() {
            val criteria = new ErgebnisseToSendCriteriaModel("wahlID", "wahlbezirkID", 0L, MeldungsartModel.V1, "hauptwahlbezirkID");

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validErgebnisseToSendCriteriaOrThrow(criteria));
        }

        @ParameterizedTest(name = "{1}")
        @MethodSource("streamWithInvalidCriteria")
        void should_throwFachlicheWlsException_when_ergebnisseToSendCriteriaAreInvalid(final ArgumentsAccessor arguments) {
            val mockedWlsException = FachlicheWlsException.withCode("000").buildWithMessage("validation failed");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SENDERGEBNISSE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.validErgebnisseToSendCriteriaOrThrow(arguments.get(0, ErgebnisseToSendCriteriaModel.class)))
                    .isSameAs(mockedWlsException);
        }

        public static Stream<Arguments> streamWithInvalidCriteria() {
            return Stream.of(
                    Arguments.of(null, "ergebnisseToSend is null"),
                    Arguments.of(new ErgebnisseToSendCriteriaModel(null, "wahlbezirkID", 0L, MeldungsartModel.V1, "hauptwahlbezirkID"), "wahlID is null"),
                    Arguments.of(new ErgebnisseToSendCriteriaModel("", "wahlbezirkID", 0L, MeldungsartModel.V1, "hauptwahlbezirkID"), "wahlID is empty"),
                    Arguments.of(new ErgebnisseToSendCriteriaModel("    ", "wahlbezirkID", 0L, MeldungsartModel.V1, "hauptwahlbezirkID"), "wahlID is blank"),
                    Arguments.of(new ErgebnisseToSendCriteriaModel("wahlID", null, 0L, MeldungsartModel.V1, "hauptwahlbezirkID"), "wahlbezirkID is null"),
                    Arguments.of(new ErgebnisseToSendCriteriaModel("wahlID", "", 0L, MeldungsartModel.V1, "hauptwahlbezirkID"), "wahlbezirkID is empty"),
                    Arguments.of(new ErgebnisseToSendCriteriaModel("wahlID", "   ", 0L, MeldungsartModel.V1, "hauptwahlbezirkID"), "wahlbezirkID is blank"),
                    Arguments.of(new ErgebnisseToSendCriteriaModel("wahlID", "wahlbezirkID", 0L, MeldungsartModel.V1, null), "hauptwahlbezirkID is null"),
                    Arguments.of(new ErgebnisseToSendCriteriaModel("wahlID", "wahlbezirkID", 0L, MeldungsartModel.V1, ""), "hauptwahlbezirkID is empty"),
                    Arguments.of(new ErgebnisseToSendCriteriaModel("wahlID", "wahlbezirkID", 0L, MeldungsartModel.V1, "    "), "hauptwahlbezirkID is blank"));
        }
    }

    @Nested
    class CheckValidation {

        @ParameterizedTest
        @ValueSource(booleans = { true, false })
        void should_returnValidatorResponseOfUWBValidator_whenWahlbezirkArtIsUWB(final boolean expectedValidationResult) {
            val wahlbezirkArt = WahlbezirkArtModel.UWB;
            val wahlart = WahlartModel.BTW;
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val waehlerverzeichnisNumemr = 0L;
            val meldungsart = MeldungsartModel.V1;

            Mockito.when(electionTypeValidations.stream()).thenReturn(Stream.of(validator));
            Mockito.when(validator.supports(wahlart)).thenReturn(true);
            Mockito.when(validator.isValidUwb(eq(wahlbezirkID), eq(wahlID), eq(waehlerverzeichnisNumemr), eq(meldungsart)))
                    .thenReturn(expectedValidationResult);

            val result = unitUnderTest.checkValidation(wahlart, wahlbezirkArt, wahlbezirkID, wahlID, waehlerverzeichnisNumemr, meldungsart);

            Assertions.assertThat(result).isEqualTo(expectedValidationResult);
        }

        @ParameterizedTest
        @ValueSource(booleans = { true, false })
        void should_returnValidatorResponseOfBWBValidator_whenWahlbezirkArtIsBWB(final boolean expectedValidationResult) {
            val wahlbezirkArt = WahlbezirkArtModel.BWB;
            val wahlart = WahlartModel.BTW;
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val waehlerverzeichnisNumemr = 0L;
            val meldungsart = MeldungsartModel.V1;

            Mockito.when(electionTypeValidations.stream()).thenReturn(Stream.of(validator));
            Mockito.when(validator.supports(wahlart)).thenReturn(true);
            Mockito.when(validator.isValidBwb(eq(wahlbezirkID), eq(wahlID), eq(waehlerverzeichnisNumemr), eq(meldungsart)))
                    .thenReturn(expectedValidationResult);

            val result = unitUnderTest.checkValidation(wahlart, wahlbezirkArt, wahlbezirkID, wahlID, waehlerverzeichnisNumemr, meldungsart);

            Assertions.assertThat(result).isEqualTo(expectedValidationResult);
        }

        @Test
        void should_throwIllegalArgumentException_whenNoValidatorIsFoundForWahlart() {
            val wahlbezirkArt = WahlbezirkArtModel.BWB;
            val wahlart = WahlartModel.BTW;
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val waehlerverzeichnisNumemr = 0L;
            val meldungsart = MeldungsartModel.V1;

            Mockito.when(electionTypeValidations.stream()).thenReturn(Stream.of(validator));
            Mockito.when(validator.supports(wahlart)).thenReturn(false);

            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.checkValidation(wahlart, wahlbezirkArt, wahlbezirkID, wahlID, waehlerverzeichnisNumemr, meldungsart))
                    .isInstanceOf(IllegalArgumentException.class);

            Mockito.verifyNoMoreInteractions(validator);
        }
    }

}
