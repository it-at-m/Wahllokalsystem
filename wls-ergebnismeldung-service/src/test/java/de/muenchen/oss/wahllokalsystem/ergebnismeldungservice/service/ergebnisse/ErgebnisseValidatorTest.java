package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.StapelartModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.Collections;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ErgebnisseValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    ErgebnisseValidator unitUnderTest;

    @Nested
    class ValidBezirkUndWahlIdStapelartOrThrow {

        @Test
        void should_notThrowException_when_bezirkUndWahlIDStapelartIsValid() {
            val id = new ErgebnisseReference("wahlID", "wahlbezirkID", Stapelart.LTW_BZW_A);
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("validation of parameters failed");

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validReferenceOrThrow(id, mockedWlsException));
        }

        @ParameterizedTest(name = "provided exception when {1}")
        @MethodSource("invalidWahlbezirkArgumentsWithTestcaseNameAppendix")
        void should_throwProvidedException_when_bezirkUndWahlIDStapelartIsNotValid(final ArgumentsAccessor arguments) {
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.validReferenceOrThrow(arguments.get(0, ErgebnisseReference.class), mockedWlsException))
                    .isSameAs(mockedWlsException);
        }

        public static Stream<Arguments> invalidWahlbezirkArgumentsWithTestcaseNameAppendix() {
            return Stream.of(
                    Arguments.of(new ErgebnisseReference("wahlbezirkID", null, Stapelart.LTW_BZW_A), "wahlID is null"),
                    Arguments.of(new ErgebnisseReference("wahlbezirkID", "", Stapelart.LTW_BZW_A), "wahlID is empty"),
                    Arguments.of(new ErgebnisseReference("wahlbezirkID", "   ", Stapelart.LTW_BZW_A), "wahlID is blank"),
                    Arguments.of(new ErgebnisseReference(null, "wahlID", Stapelart.LTW_BZW_A), "wahlbezirkID is null"),
                    Arguments.of(new ErgebnisseReference("", "wahlID", Stapelart.LTW_BZW_A), "wahlbezirkID is is empty"),
                    Arguments.of(new ErgebnisseReference("   ", "wahlID", Stapelart.LTW_BZW_A), "wahlbezirkID is blank"),
                    Arguments.of(new ErgebnisseReference("wahlbezirkID", "wahlID", null), "stapelart is null"));
        }
    }

    @Nested
    class ValidWahlbezirkIDAndWahlIDOrThrow {

        @Test
        void should_notThrowException_when_bezirkUndWahlIDIsValid() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("validation of parameters failed");

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validIDOrThrow(wahlbezirkID, wahlID, mockedWlsException));
        }

        @Test
        void should_throwProvidedException_when_bezirkIDIsBlank() {
            val wahlbezirkID = " ";
            val wahlID = "wahlID";
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.validIDOrThrow(wahlbezirkID, wahlID, mockedWlsException))
                    .isSameAs(mockedWlsException);
        }

        @Test
        void should_throwProvidedException_when_wahlIDIsBlank() {
            val wahlbezirkID = "";
            val wahlID = " ";
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.validIDOrThrow(wahlbezirkID, wahlID, mockedWlsException))
                    .isSameAs(mockedWlsException);
        }

        @Test
        void should_throwProvidedException_when_bezirkIDIsNull() {
            val wahlID = "wahlID";
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.validIDOrThrow(null, wahlID, mockedWlsException))
                    .isSameAs(mockedWlsException);
        }

        @Test
        void should_throwProvidedException_when_wahlIDIsNull() {
            val wahlbezirkID = "wahlbezirkID";
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.validIDOrThrow(wahlbezirkID, null, mockedWlsException))
                    .isSameAs(mockedWlsException);
        }

        @Test
        void should_throwProvidedException_when_bezirkIDIsEmpty() {
            val wahlbezirkID = "";
            val wahlID = "wahlID";
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.validIDOrThrow(wahlbezirkID, wahlID, mockedWlsException))
                    .isSameAs(mockedWlsException);
        }

        @Test
        void should_throwProvidedException_when_wahlIDIsEmpty() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "";
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.validIDOrThrow(wahlbezirkID, wahlID, mockedWlsException))
                    .isSameAs(mockedWlsException);
        }
    }

    @Nested
    class ValidErgebnisseOrThrow {

        @Test
        void should_throwException_when_ergebnisseIsEmpty() {
            val ergebnisseModelToValidate = new ErgebnisseModel("", "", StapelartModel.LTW_BZW_A, Collections.emptyList());

            val mockedFachlicheWlsException = FachlicheWlsException.withCode("").buildWithMessage("sth failed");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_ERGEBNISSE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedFachlicheWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validModelOrThrow(ergebnisseModelToValidate))
                    .isSameAs(mockedFachlicheWlsException);
        }

        @Test
        void should_throwFachlicheWlsException_when_ergebnisseIsNull() {
            val ergebnisseModelModelToValidate = new ErgebnisseModel("wahlbezirkID", "wahlID", StapelartModel.LTW_BZW_A, null);

            val mockedFachlicheWlsException = FachlicheWlsException.withCode("").buildWithMessage("sth failed");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_ERGEBNISSE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedFachlicheWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validModelOrThrow(ergebnisseModelModelToValidate))
                    .isSameAs(mockedFachlicheWlsException);
        }
    }
}
