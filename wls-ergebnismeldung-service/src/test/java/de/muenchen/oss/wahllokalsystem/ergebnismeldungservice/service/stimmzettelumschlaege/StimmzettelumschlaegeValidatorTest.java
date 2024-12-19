package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelumschlaege;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
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
class StimmzettelumschlaegeValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    StimmzettelumschlaegeValidator unitUnderTest;

    @Nested
    class ValidBezirkUndWahlIdOrThrow {

        final FachlicheWlsException providedException = FachlicheWlsException.withCode("").buildWithMessage("sth failed");

        @Test
        void should_notThrowException_when_bezirkUndWahlIDIsValid() {
            val id = new BezirkUndWahlID("wahlID", "wahlbezirkID");

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validBezirkUndWahlIdOrThrow(id, null));
        }

        @ParameterizedTest(name = "provided exception when {1}")
        @MethodSource("invalidWahlbezirkArgumentsWithTestcaseNameAppendix")
        void should_throwProvidedException_when_bezirkUndWahlIdIsNotValid(final ArgumentsAccessor arguments) {
            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.validBezirkUndWahlIdOrThrow(arguments.get(0, BezirkUndWahlID.class), providedException))
                    .isSameAs(providedException);
        }

        public static Stream<Arguments> invalidWahlbezirkArgumentsWithTestcaseNameAppendix() {
            return Stream.of(
                    Arguments.of(null, "argument is null"),
                    Arguments.of(new BezirkUndWahlID(null, "wahlbezirkID"), "wahlID is null"),
                    Arguments.of(new BezirkUndWahlID("", "wahlbezirkID"), "wahlID is empty"),
                    Arguments.of(new BezirkUndWahlID("   ", "wahlbezirkID"), "wahlID is blank"),
                    Arguments.of(new BezirkUndWahlID("wahlID", null), "wahlbezirkID is null"),
                    Arguments.of(new BezirkUndWahlID("wahlID", ""), "wahlbezirkID is is empty"),
                    Arguments.of(new BezirkUndWahlID("wahlID", "   "), "wahlbezirkID is blank"));
        }
    }

    @Nested
    class ValidStimmzettelumschlaegeOrThrow {

        @Test
        void should_notThrowException_when_stimmzettelumschlaegeIsEmptyButNotNull() {
            val stimmzettelumschlaegeModelToValidate = new StimmzettelumschlaegeModel(null, null, 0, 0);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validStimmzettelumschlaegeOrThrow(stimmzettelumschlaegeModelToValidate));
        }

        @Test
        void should_throwFachlicheWlsException_when_stimmzettelumschlaegeIsNull() {
            val mockedFachlicheWlsException = FachlicheWlsException.withCode("").buildWithMessage("sth failed");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_STIMMZETTELUMSCHLAEGE_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedFachlicheWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validStimmzettelumschlaegeOrThrow(null)).isSameAs(mockedFachlicheWlsException);
        }
    }
}
