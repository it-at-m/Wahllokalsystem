package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.wahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.wahlvorbereitung.client.UrnenwahlSchliessungsUhrzeitControllerApi;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.wahlvorbereitung.model.UrnenwahlSchliessungsUhrzeitDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import jakarta.annotation.Nonnull;
import java.time.LocalDateTime;
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
class WahlvorbereitungClientImplTest {

    @Mock
    UrnenwahlSchliessungsUhrzeitControllerApi urnenwahlSchliessungsUhrzeitControllerApi;

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    WahlvorbereitungClientImpl unitUnderTest;

    @Nested
    class IsWahlbezirkGeschlossen {

        @Test
        void should_returnTrue_when_urnenwahlSchliessungsUhrzeitIsNotNull() {
            val wahlbezirkID = "wahlbezirkID";

            Mockito.when(urnenwahlSchliessungsUhrzeitControllerApi.getUrnenwahlSchliessungsUhrzeit(wahlbezirkID))
                    .thenReturn(new UrnenwahlSchliessungsUhrzeitDTO().urnenwahlSchliessungsUhrzeit(
                            LocalDateTime.now()));

            val result = unitUnderTest.isWahlbezirkGeschlossen(wahlbezirkID);

            Assertions.assertThat(result).isTrue();
        }

        @Test
        void should_dontRequireAdditionalChecksOnSchliessungsUhrzeit_when_propertyUrnenwahlSchliessungsUhrzeitIsAnnotatedWithNonnull()
                throws NoSuchFieldException {
            Assertions.assertThat(
                    UrnenwahlSchliessungsUhrzeitDTO.class.getDeclaredField("urnenwahlSchliessungsUhrzeit").getAnnotation(Nonnull.class))
                    .isNotNull();
        }

        @Test
        void should_rethrowWlsException_when_apiThrewWlsException() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedApiWlsException = FachlicheWlsException.withCode("000").buildWithMessage("");
            Mockito.doThrow(mockedApiWlsException).when(urnenwahlSchliessungsUhrzeitControllerApi).getUrnenwahlSchliessungsUhrzeit(wahlbezirkID);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.isWahlbezirkGeschlossen(wahlbezirkID)).isSameAs(mockedApiWlsException);
        }

        @Test
        void should_throwWlsException_when_apiThrewNonWlsException() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("");

            Mockito.doThrow(new RuntimeException("api call failed")).when(urnenwahlSchliessungsUhrzeitControllerApi)
                    .getUrnenwahlSchliessungsUhrzeit(wahlbezirkID);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_WAHLVORBEREITUNG))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.isWahlbezirkGeschlossen(wahlbezirkID)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwFachlicheWlsException_when_apiReturnsNull() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedWlsException = FachlicheWlsException.withCode("000").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.WAHLVORBEREITUNG_SCHLIESSUNGSUHRZEIT_NULL_OR_EMPTY))
                    .thenReturn(mockedWlsException);
            Mockito.when(urnenwahlSchliessungsUhrzeitControllerApi.getUrnenwahlSchliessungsUhrzeit(wahlbezirkID)).thenReturn(null);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.isWahlbezirkGeschlossen(wahlbezirkID)).isSameAs(mockedWlsException);
        }
    }

}
