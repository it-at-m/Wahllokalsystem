package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.begruendung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
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
class BegruendungValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    BegruendungValidator unitUnderTest;

    @Nested
    class ValidBezirkUndWahlIdStapelartOrThrow {


        @Test
        void should_notThrowException_when_bezirkUndWahlIDStapelartIsValid() {
            val id = new BegruendungReference("wahlID", "wahlbezirkID", Stapelart.LTW_BZW_A);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validReferenceOrThrow(id));
        }

        @ParameterizedTest(name = "provided exception when {1}")
        @MethodSource("invalidWahlbezirkArgumentsWithTestcaseNameAppendix")
        void should_throwProvidedException_when_bezirkUndWahlIDStapelartIsNotValid(final ArgumentsAccessor arguments) {
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GET_BEGRUENDUNG_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);
            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.validReferenceOrThrow(arguments.get(0, BegruendungReference.class)))
                            .isSameAs(mockedWlsException);
        }

        public static Stream<Arguments> invalidWahlbezirkArgumentsWithTestcaseNameAppendix() {
            return Stream.of(
                    Arguments.of(new BegruendungReference("wahlbezirkID", null, Stapelart.LTW_BZW_A), "wahlID is null"),
                    Arguments.of(new BegruendungReference("wahlbezirkID", "", Stapelart.LTW_BZW_A), "wahlID is empty"),
                    Arguments.of(new BegruendungReference("wahlbezirkID", "   ", Stapelart.LTW_BZW_A), "wahlID is blank"),
                    Arguments.of(new BegruendungReference(null, "wahlID", Stapelart.LTW_BZW_A), "wahlbezirkID is null"),
                    Arguments.of(new BegruendungReference("", "wahlID", Stapelart.LTW_BZW_A), "wahlbezirkID is is empty"),
                    Arguments.of(new BegruendungReference("   ", "wahlID", Stapelart.LTW_BZW_A), "wahlbezirkID is blank"),
                    Arguments.of(new BegruendungReference("wahlbezirkID", "wahlID", null), "stapelart is null"));
        }
    }

    @Nested
    class ValidBegruendungOrThrow {

        @Test
        void should_notThrowException_when_begruendungIsEmptyButNotNull() {
            val begruendungModelModelToValidate = new BegruendungModel("", "", Stapelart.LTW_BZW_A, "", "", true, true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validModelOrThrow(begruendungModelModelToValidate));
        }

        @Test
        void should_throwFachlicheWlsException_when_grundIsNull() {
            val begruendungModelModelToValidate = new BegruendungModel("wahlbezirkID", "wahlID", Stapelart.LTW_BZW_A, null, null, true, true);

            val mockedFachlicheWlsException = FachlicheWlsException.withCode("").buildWithMessage("sth failed");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_BEGRUENDUNG_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedFachlicheWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validModelOrThrow(begruendungModelModelToValidate)).isSameAs(mockedFachlicheWlsException);
        }

        @Test
        void should_notThrowFachlicheWlsException_when_onlyOneGrundIsNull() {
            val begruendungModelModelToValidate = new BegruendungModel("wahlbezirkID", "wahlID", Stapelart.LTW_BZW_A, null, "grund2", true, true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validModelOrThrow(begruendungModelModelToValidate));
        }
    }
}
