package de.muenchen.oss.wahllokalsystem.adminservice.service.wahllokalbenutzer;

import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
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
class WahllokalBenutzerValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    private WahllokalBenutzerValidator unitUnderTest;

    @Nested
    class ValidWahltagIDParamOrThrow {

        @Test
        void should_notThrowException_when_wahltagIDIsValid() {
            val wahltagID = "wahltagID";

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validWahltagIDParamOrThrow(wahltagID));
        }

        @ParameterizedTest(name = "provided exception when {1}")
        @MethodSource("invalidWahltagIDArgumentsWithTestcaseNameAppendix")
        void should_throwFachlicheWlsException_when_wahltagIDIsNotValid(final ArgumentsAccessor arguments) {

            val mockedException = FachlicheWlsException.withCode("165").buildWithMessage("Parameter fehlt.");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.MISSING_ARGUMENT)).thenReturn(mockedException);

            Assertions.assertThatException()
                    .isThrownBy(
                            () -> unitUnderTest.validWahltagIDParamOrThrow(arguments.get(0, String.class)))
                    .isSameAs(mockedException);
        }

        public static Stream<Arguments> invalidWahltagIDArgumentsWithTestcaseNameAppendix() {
            return Stream.of(
                    Arguments.of(null, "wahltagID is null"),
                    Arguments.of((""), "wahltagID is empty"),
                    Arguments.of(("   "), "wahltagID is blank"));
        }
    }
}