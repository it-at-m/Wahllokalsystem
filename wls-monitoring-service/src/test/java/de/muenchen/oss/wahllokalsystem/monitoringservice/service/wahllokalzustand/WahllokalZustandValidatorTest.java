package de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WahllokalZustandValidatorTest {

    private final FachlicheWlsException mockedFachlicheWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

    @InjectMocks
    WahllokalZustandValidator unitUnderTest;

    @Nested
    class ValidWahlbezirkIDOrThrow {

        @Test
        void should_notThrowAnyException_when_wahlbezirkIdIsNotNullOrBlankOrEmpty() {
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validWahlbezirkIDOrThrow("wahlbezirkID", mockedFachlicheWlsException));
        }

        @Test
        void should_throwGivenException_when_wahlbezirkIdIsNull() {
            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlbezirkIDOrThrow(null, mockedFachlicheWlsException))
                    .isSameAs(mockedFachlicheWlsException);

        }

        @Test
        void should_throwGivenException_when_wahlbezirkIdIsBlank() {
            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlbezirkIDOrThrow("   ", mockedFachlicheWlsException))
                    .isSameAs(mockedFachlicheWlsException);
        }

        @Test
        void should_throwGivenException_when_wahlbezirkIdIsEmpty() {
            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlbezirkIDOrThrow("", mockedFachlicheWlsException))
                    .isSameAs(mockedFachlicheWlsException);
        }
    }

    @Nested
    class ValidWahlIdUndWahlbezirkIDOrThrow {
    
        @Test
        void should_notThrowAnyException_when_wahlIdAndWahlbezirkIdAreNotNullOrBlankOrEmpty() {
            Assertions.assertThatNoException()
                    .isThrownBy(() -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(new BezirkUndWahlID("wahlID01", "wahlbezirkID01"),
                            mockedFachlicheWlsException));
        }

        @Test
        void should_throwGivenException_when_paramBezirkUndWahlIdIsNull() {
            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(null, mockedFachlicheWlsException))
                    .isSameAs(mockedFachlicheWlsException);

        }

        @Test
        void should_throwGivenException_when_wahlIdIsNull() {
            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(new BezirkUndWahlID(null, "wahlbezirkID01"), mockedFachlicheWlsException))
                    .isSameAs(mockedFachlicheWlsException);
        }

        @Test
        void should_throwGivenException_when_wahlIdIsBlank() {
            Assertions.assertThatException()
                    .isThrownBy(
                            () -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(new BezirkUndWahlID("   ", "wahlbezirkID01"), mockedFachlicheWlsException))
                    .isSameAs(mockedFachlicheWlsException);
        }

        @Test
        void should_throwGivenException_when_wahlIdIsEmpty() {
            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(new BezirkUndWahlID("", "wahlbezirkID01"), mockedFachlicheWlsException))
                    .isSameAs(mockedFachlicheWlsException);
        }

        @Test
        void should_throwGivenException_when_wahlbezirkIdIsNull() {
            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(new BezirkUndWahlID("wahlID01", null), mockedFachlicheWlsException))
                    .isSameAs(mockedFachlicheWlsException);
        }

        @Test
        void should_throwGivenException_when_wahlbezirkIdIsBlank() {
            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(new BezirkUndWahlID("wahlID01", "   "), mockedFachlicheWlsException))
                    .isSameAs(mockedFachlicheWlsException);
        }

        @Test
        void should_throwGivenException_when_wahlbezirkIdIsEmpty() {
            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.validWahlIdUndWahlbezirkIDOrThrow(new BezirkUndWahlID("wahlID01", ""), mockedFachlicheWlsException))
                    .isSameAs(mockedFachlicheWlsException);
        }
    }
}
