package de.muenchen.oss.wahllokalsystem.eaiservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.eaiservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.dto.ErgebnismeldungDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
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
class ErgebnismeldungValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    ErgebnismeldungValidator unitUnderTest;

    @Nested
    class SaveErgebnismeldungParameterOrThrow {

        @Test
        void noExceptionWhenErgebnismeldungIsValid() {
            val validErgebnismeldung = ErgebnismeldungDTO.builder().wahlbezirkID("00000000-0000-0000-0000-000000000001").wahlID("wahlID1").meldungsart(null).aWerte(null).bWerte(null).wahlbriefeWerte(null).ungueltigeStimmzettels(null).ungueltigeStimmzettelAnzahl(null).ergebnisse(null).wahlart(null).build();

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validDTOToSetOrThrow(validErgebnismeldung));
        }

        @Test
        void exceptionWhenErgebnismeldungIsNull() {
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.exception.ExceptionConstants.DATENALLGEMEIN_PARAMETER_FEHLEN)).thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validDTOToSetOrThrow(null)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionWhenWahlbezirkIDIsBlank() {
            val ergebnismeldungWithBlankWahlbezirkID = ErgebnismeldungDTO.builder().wahlbezirkID(" ").wahlID("wahlID1").meldungsart(null).aWerte(null).bWerte(null).wahlbriefeWerte(null).ungueltigeStimmzettels(null).ungueltigeStimmzettelAnzahl(null).ergebnisse(null).wahlart(null).build();

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEWAHLERGEBNISMELDUNG_WAHLBEZIRKID_FEHLT)).thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validDTOToSetOrThrow(ergebnismeldungWithBlankWahlbezirkID)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptionWhenWahlIDIsBlank() {

            val ergebnismeldungWithBlankID = ErgebnismeldungDTO.builder().wahlbezirkID("00000000-0000-0000-0000-000000000001").wahlID(" ").meldungsart(null).aWerte(null).bWerte(null).wahlbriefeWerte(null).ungueltigeStimmzettels(null).ungueltigeStimmzettelAnzahl(null).ergebnisse(null).wahlart(null).build();
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEWAHLERGEBNISMELDUNG_WAHLID_FEHLT)).thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validDTOToSetOrThrow(ergebnismeldungWithBlankID)).isSameAs(mockedWlsException);
        }

    }
}
