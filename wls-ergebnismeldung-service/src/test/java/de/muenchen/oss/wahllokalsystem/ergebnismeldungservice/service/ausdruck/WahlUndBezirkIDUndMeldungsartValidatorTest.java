package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Meldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndMeldungsart;
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
class WahlUndBezirkIDUndMeldungsartValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    WahlUndBezirkIDUndMeldungsartValidator unitUnderTest;

    @Nested
    class validWahlUndBezirkIDUndMeldungsartOrThrow {

        @Test
        void should_throwFachlicheWlsException_when_meldungsartIsNull() {
            val wahlUndBezirkIDUndMeldungsartToValidate = new WahlUndBezirkIDUndMeldungsart("wahlbezirkID", "wahlID", null);

            val mockedFachlicheWlsException = FachlicheWlsException.withCode("").buildWithMessage("sth failed");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GET_AUSDRUCK_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedFachlicheWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlUndBezirkIDUndMeldungsartOrThrow(wahlUndBezirkIDUndMeldungsartToValidate))
                    .isSameAs(mockedFachlicheWlsException);
        }

        @ParameterizedTest(name = "provided exception when {1}")
        @MethodSource("invalidWahlUndBezirkIDUndMeldungsartArgumentsWithTestcaseNameAppendix")
        void should_throwFachlicheWlsException_when_wahlUndBezirkIDUndIsNotValid(final ArgumentsAccessor arguments) {
            val mockedFachlicheWlsException = FachlicheWlsException.withCode("").buildWithMessage("sth failed");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GET_AUSDRUCK_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedFachlicheWlsException);

            Assertions.assertThatException()
                    .isThrownBy(
                            () -> unitUnderTest.validWahlUndBezirkIDUndMeldungsartOrThrow(arguments.get(0, WahlUndBezirkIDUndMeldungsart.class)))
                    .isSameAs(mockedFachlicheWlsException);
        }

        public static Stream<Arguments> invalidWahlUndBezirkIDUndMeldungsartArgumentsWithTestcaseNameAppendix() {
            return Stream.of(
                    Arguments.of(new WahlUndBezirkIDUndMeldungsart(null, "wahlID", Meldungsart.V1), "wahlbezirkID is null"),
                    Arguments.of(new WahlUndBezirkIDUndMeldungsart("", "wahlID", Meldungsart.V1), "wahlbezirkID is empty"),
                    Arguments.of(new WahlUndBezirkIDUndMeldungsart("   ", "wahlID", Meldungsart.V1), "wahlbezirkID is blank"),
                    Arguments.of(new WahlUndBezirkIDUndMeldungsart("wahlbezirkID", null, Meldungsart.V1), "wahlID is null"),
                    Arguments.of(new WahlUndBezirkIDUndMeldungsart("wahlbezirkID", "", Meldungsart.V1), "wahlID is is empty"),
                    Arguments.of(new WahlUndBezirkIDUndMeldungsart("wahlbezirkID", "   ", Meldungsart.V1), "wahlID is blank"));
        }
    }
}
