package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.basisdaten;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.basisdaten.client.WahlenControllerApi;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.basisdaten.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlartModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.Collections;
import java.util.List;
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
class BasisdatenClientImplTest {

    @Mock
    WahlenControllerApi wahlenControllerApi;
    @Mock
    ExceptionFactory exceptionFactory;
    @Mock
    BasisdatenClientMapper basisdatenClientMapper;

    @InjectMocks
    BasisdatenClientImpl unitUnderTest;

    @Nested
    class GetWahlart {

        private final String wahlID = "wahlID";
        private final String wahltagID = "wahltagID";

        @Test
        void should_returnWahlartOfWahl_when_wahlWithIDExists() {
            val mockedWahlen = List.of(new WahlDTO().wahlart(WahlDTO.WahlartEnum.BTW).wahlID("other" + wahlID),
                    new WahlDTO().wahlart(WahlDTO.WahlartEnum.LTW).wahlID(wahlID),
                    new WahlDTO().wahlart(WahlDTO.WahlartEnum.EUW).wahlID("another" + wahlID));
            Mockito.when(wahlenControllerApi.getWahlen(wahltagID)).thenReturn(mockedWahlen);
            Mockito.when(basisdatenClientMapper.toModel(WahlDTO.WahlartEnum.LTW)).thenReturn(WahlartModel.LTW);

            val result = unitUnderTest.getWahlart(wahltagID, wahlID);

            Assertions.assertThat(result).isEqualTo(WahlartModel.LTW);
        }

        @Test
        void should_throwFachlicheWlsException_when_noWahlWithGivenIDExistsOnCurrentWahltag() {
            val mockedWahlen = List.of(new WahlDTO().wahlart(WahlDTO.WahlartEnum.BTW).wahlID("other" + wahlID),
                    new WahlDTO().wahlart(WahlDTO.WahlartEnum.EUW).wahlID("another" + wahlID));
            val mockedWlsException = FachlicheWlsException.withCode("000").buildWithMessage("no wahl with wahlID given");

            Mockito.when(wahlenControllerApi.getWahlen(wahltagID)).thenReturn(mockedWahlen);
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.BASISDATEN_WAHL_NOT_FOUND)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlart(wahltagID, wahlID)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwFachlicheWlsException_when_noWahlenExistsOnCurrentWahltag() {
            final List<WahlDTO> mockedWahlen = Collections.emptyList();
            val mockedWlsException = FachlicheWlsException.withCode("000").buildWithMessage("no wahlen on current wahltag");

            Mockito.when(wahlenControllerApi.getWahlen(wahltagID)).thenReturn(mockedWahlen);
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.BASISDATEN_WAHLEN_EMPTY)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlart(wahltagID, wahlID)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwTechnischeWlsException_when_nonWlsExceptionIsThrownFromWahlenApi() {
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with wahlen api failed");

            Mockito.doThrow(new RuntimeException("api call failed")).when(wahlenControllerApi).getWahlen(wahltagID);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_BASISDATEN))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlart(wahltagID, wahlID)).isSameAs(mockedWlsException);
        }

        @Test
        void should_rethrowWlsException_when_wlsExceptionIsThrownFromWahlenApi() {
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with wahlen api failed");

            Mockito.doThrow(mockedWlsException).when(wahlenControllerApi).getWahlen(wahltagID);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlart(wahltagID, wahlID)).isSameAs(mockedWlsException);
        }
    }
}
