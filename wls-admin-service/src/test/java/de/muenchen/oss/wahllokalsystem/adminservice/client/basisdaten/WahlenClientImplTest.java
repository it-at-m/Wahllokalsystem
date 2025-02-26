package de.muenchen.oss.wahllokalsystem.adminservice.client.basisdaten;

import de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.client.WahlenControllerApi;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDate;
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
class WahlenClientImplTest {

    @Mock
    WahlenControllerApi wahlenControllerApi;

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    WahlenClientMapper wahlenClientMapper;

    @InjectMocks
    WahlenClientImpl unitUnderTest;

    @Nested
    class ResetWahlen {

        @Test
        void should_resetWahlen_when_noExceptionIsThrown() {
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.resetWahlen());

            Mockito.verify(wahlenControllerApi).resetWahlen();
        }

        @Test
        void should_throwTechnischeWlsException_when_wlsExceptionIsThrownFromWahlenApi() {
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with wahlen api failed");

            Mockito.doThrow(mockedWlsException).when(wahlenControllerApi).resetWahlen();

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.resetWahlen()).isSameAs(mockedWlsException);
        }

        @Test
        void should_rethrowWlsException_when_wlsExceptionIsThrownFromWahlenApi() {
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with wahlen api failed");

            Mockito.doThrow(new RuntimeException("api call failed")).when(wahlenControllerApi).resetWahlen();
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_BASISDATEN))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.resetWahlen()).isSameAs(mockedWlsException);
        }
    }

    @Nested
    class GetWahlen {

        @Test
        void should_returnWahlen_when_wahlenExists() {
            val wahlID = "wahlID";
            val nowDate = LocalDate.now();
            val mockedWahlDtoList = List.of(
                    new WahlDTO().wahltag(nowDate).wahlart(WahlDTO.WahlartEnum.BTW).wahlID(wahlID),
                    new WahlDTO().wahltag(nowDate).wahlart(WahlDTO.WahlartEnum.LTW).wahlID(wahlID),
                    new WahlDTO().wahltag(nowDate).wahlart(WahlDTO.WahlartEnum.EUW).wahlID(wahlID));
            val mockedWahlModelList = wahlenClientMapper.toModelList(mockedWahlDtoList);

            Mockito.when(wahlenControllerApi.getWahlen(wahlID)).thenReturn(mockedWahlDtoList);
            Mockito.when(wahlenClientMapper.toModelList(mockedWahlDtoList)).thenReturn(mockedWahlModelList);

            val result = unitUnderTest.getWahlen(wahlID);

            Assertions.assertThat(result).isEqualTo(mockedWahlModelList);
        }

        @Test
        void should_rethrowWlsException_when_wlsExceptionIsThrownFromWahlenApi() {
            val wahlID = "wahlID";
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with wahlen api failed");

            Mockito.doThrow(mockedWlsException).when(wahlenControllerApi).getWahlen(wahlID);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlen(wahlID)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwTechnischeWlsException_when_nonWlsExceptionIsThrownFromWahlenApi() {
            val wahlID = "wahlID";
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with wahlen api failed");

            Mockito.doThrow(new RuntimeException("api call failed")).when(wahlenControllerApi).getWahlen(wahlID);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_BASISDATEN))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlen(wahlID)).isSameAs(mockedWlsException);
        }
    }

    @Nested
    class PostWahlen {

        @Test
        void should_callWahlenAPIWithoutException_when_dataIsGiven() {
            val wahlID = "wahlID";
            val nowDate = LocalDate.now();
            val mockedWahlDtoList = List.of(
                    new WahlDTO().wahltag(nowDate).wahlart(WahlDTO.WahlartEnum.BTW).wahlID(wahlID),
                    new WahlDTO().wahltag(nowDate).wahlart(WahlDTO.WahlartEnum.LTW).wahlID(wahlID),
                    new WahlDTO().wahltag(nowDate).wahlart(WahlDTO.WahlartEnum.EUW).wahlID(wahlID));
            val mockedWahlModelList = wahlenClientMapper.toModelList(mockedWahlDtoList);

            Mockito.when(wahlenClientMapper.toDtoList(mockedWahlModelList)).thenReturn(mockedWahlDtoList);

            unitUnderTest.postWahlen(wahlID, mockedWahlModelList);

            Mockito.verify(wahlenControllerApi, Mockito.times(1)).postWahlen(wahlID, mockedWahlDtoList);
        }

        @Test
        void should_rethrowWlsException_when_wlsExceptionIsThrownFromWahlenApi() {
            val wahlID = "wahlID";
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with wahlen api failed");

            Mockito.when(wahlenClientMapper.toDtoList(null)).thenReturn(null);
            Mockito.doThrow(mockedWlsException).when(wahlenControllerApi).postWahlen(wahlID, null);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.postWahlen(wahlID, null)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwTechnischeWlsException_when_nonWlsExceptionIsThrownFromWahlenApi() {
            val wahlID = "wahlID";
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with wahlen api failed");

            Mockito.doThrow(new RuntimeException("api call failed")).when(wahlenControllerApi).postWahlen(wahlID, null);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_BASISDATEN))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.postWahlen(wahlID, null)).isSameAs(mockedWlsException);
        }
    }
}
