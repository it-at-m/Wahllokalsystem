package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlbeteiligung;

import de.muenchen.oss.wahllokalsystem.eaiservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlbeteiligung.dto.WahlbeteiligungsMeldungDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
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
class WahlbeteiligungValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    WahlbeteiligungValidator unitUnderTest;

    @Nested
    class validDTOToSetOrThrow {

        @Test
        void should_throwWlsException_when_wahlbeteiligungsMeldungDTOIsNull() {
            val expectedException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(
                    de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.exception.ExceptionConstants.DATENALLGEMEIN_PARAMETER_FEHLEN))
                    .thenReturn(expectedException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validDTOToSetOrThrow(null)).isSameAs(expectedException);
        }

        @Test
        void should_notThrowException_when_wahlbeteiligungsMeldungDTOIsValid() {
            val wahlID = "wahlID1";
            val wahlbezirkID = "00000000-0000-0000-0000-000000000001";
            val anzahlWaehler = 150;
            val meldeZeitpunkt = LocalDateTime.now();

            val dtoToValidate = new WahlbeteiligungsMeldungDTO(wahlID, wahlbezirkID, anzahlWaehler, meldeZeitpunkt);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validDTOToSetOrThrow(dtoToValidate));
        }

        @Test
        void should_throwWlsException_when_wahlIDIsNull() {
            val wahlbezirkID = "00000000-0000-0000-0000-000000000001";
            val anzahlWaehler = 150;
            val meldeZeitpunkt = LocalDateTime.now();

            val dtoToValidate = new WahlbeteiligungsMeldungDTO(null, wahlbezirkID, anzahlWaehler, meldeZeitpunkt);

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEWAHLBETEILIGUNG_WAHLID_FEHLT)).thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validDTOToSetOrThrow(dtoToValidate)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwWlsException_when_wahlIDIsEmpty() {
            val wahlID = "";
            val wahlbezirkID = "00000000-0000-0000-0000-000000000001";
            val anzahlWaehler = 150;
            val meldeZeitpunkt = LocalDateTime.now();

            val dtoToValidate = new WahlbeteiligungsMeldungDTO(wahlID, wahlbezirkID, anzahlWaehler, meldeZeitpunkt);

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEWAHLBETEILIGUNG_WAHLID_FEHLT)).thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validDTOToSetOrThrow(dtoToValidate)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwWlsException_when_wahlIDIsBlank() {
            val wahlID = " ";
            val wahlbezirkID = "00000000-0000-0000-0000-000000000001";
            val anzahlWaehler = 150;
            val meldeZeitpunkt = LocalDateTime.now();

            val dtoToValidate = new WahlbeteiligungsMeldungDTO(wahlID, wahlbezirkID, anzahlWaehler, meldeZeitpunkt);

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEWAHLBETEILIGUNG_WAHLID_FEHLT)).thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validDTOToSetOrThrow(dtoToValidate)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwWlsException_when_wahlbezirkIDIsNull() {
            val wahlID = "wahlID1";
            val anzahlWaehler = 150;
            val meldeZeitpunkt = LocalDateTime.now();

            val dtoToValidate = new WahlbeteiligungsMeldungDTO(wahlID, null, anzahlWaehler, meldeZeitpunkt);

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEWAHLBETEILIGUNG_WAHLBEZIRKID_FEHLT))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validDTOToSetOrThrow(dtoToValidate)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwWlsException_when_wahlbezirkIDIsEmpty() {
            val wahlID = "wahlID1";
            val wahlbezirkID = "";
            val anzahlWaehler = 150;
            val meldeZeitpunkt = LocalDateTime.now();

            val dtoToValidate = new WahlbeteiligungsMeldungDTO(wahlID, wahlbezirkID, anzahlWaehler, meldeZeitpunkt);

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEWAHLBETEILIGUNG_WAHLBEZIRKID_FEHLT))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validDTOToSetOrThrow(dtoToValidate)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwWlsException_when_wahlbezirkIDIsBlank() {
            val wahlID = "wahlID1";
            val wahlbezirkID = " ";
            val anzahlWaehler = 150;
            val meldeZeitpunkt = LocalDateTime.now();

            val dtoToValidate = new WahlbeteiligungsMeldungDTO(wahlID, wahlbezirkID, anzahlWaehler, meldeZeitpunkt);

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEWAHLBETEILIGUNG_WAHLBEZIRKID_FEHLT))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validDTOToSetOrThrow(dtoToValidate)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwWlsException_when_meldezeitpunktIsNull() {
            val wahlID = "wahlID1";
            val wahlbezirkID = "00000000-0000-0000-0000-000000000001";
            val anzahlWaehler = 150;

            val dtoToValidate = new WahlbeteiligungsMeldungDTO(wahlID, wahlbezirkID, anzahlWaehler, null);

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEWAHLBETEILIGUNG_MELDEZEITPUNKT_FEHLT))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validDTOToSetOrThrow(dtoToValidate)).isSameAs(mockedWlsException);
        }
    }
}
