package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
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
class StimmabgabevermerkeValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    StimmabgabevermerkeValidator underTest;

    @Nested
    class ValidBezirkIDUndWaehlerverzeichnisnummerOrThrow {

        @Test
        void should_notThrowException_when_parameterIsValid() {
            val id = new BezirkIDUndWaehlerverzeichnisNummer("wahlbezirkID", 1L);
            val givenWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            underTest.validBezirkIDUndWaehlerverzeichnisnummerOrThrow(id, givenWlsException);

        }

        @Test
        void should_throwGivenException_when_parameterIsNull() {
            val givenWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Assertions.assertThatThrownBy(() -> underTest.validBezirkIDUndWaehlerverzeichnisnummerOrThrow(null, givenWlsException)).isSameAs(givenWlsException);
        }

        @Test
        void should_throwGivenException_when_waehlerverzeichnisnummerIsNull() {
            val id = new BezirkIDUndWaehlerverzeichnisNummer("wahlbezirkID", null);
            val givenWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Assertions.assertThatThrownBy(() -> underTest.validBezirkIDUndWaehlerverzeichnisnummerOrThrow(id, givenWlsException)).isSameAs(givenWlsException);
        }

        @ParameterizedTest(name = "wahlbezirkID {1}")
        @MethodSource("argumentsToCheckBlankWahlbezirkID")
        void should_throwGivenException_when_wahlbezirkIDIsBlank(final ArgumentsAccessor arguments) {
            val givenWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            val id = new BezirkIDUndWaehlerverzeichnisNummer(arguments.get(0, String.class), 1L);

            Assertions.assertThatThrownBy(() -> underTest.validBezirkIDUndWaehlerverzeichnisnummerOrThrow(id, givenWlsException)).isSameAs(givenWlsException);
        }

        public static Stream<Arguments> argumentsToCheckBlankWahlbezirkID() {
            return Stream.of(Arguments.of(null, "is null"),
                    Arguments.of("", "is empty string"),
                    Arguments.of("   ", "is blank string"));
        }
    }

    @Nested
    class ValidStimmabgabevermerkeOrThrow {

        @Test
        void should_notThrowAnyException_when_parameterIsValid() {
            val stimmabgabevermerke = new StimmabgabevermerkeModel(new BezirkIDUndWaehlerverzeichnisNummer("wahlbezirkID", 0L), 0L, Collections.emptySet());

            underTest.validStimmabgabevermerkeOrThrow(stimmabgabevermerke);
        }

        @Test
        void should_throwFachlicheWlsException_when_parameterIsNull() {
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_STATUS_PARAMETER_UNVOLLSTAENDIG)).thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> underTest.validStimmabgabevermerkeOrThrow(null)).isSameAs(mockedWlsException);
        }
    }

}
