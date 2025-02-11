package de.muenchen.oss.wahllokalsystem.adminservice.client.basisdaten;

import de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.client.WahlbezirkeControllerApi;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.model.WahlbezirkDTO;
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
class WahlbezirkeClientImplTest {

    @Mock
    WahlbezirkeControllerApi wahlbezirkeControllerApi;

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    WahlbezirkeClientMapper wahlbezirkeClientMapper;

    @InjectMocks
    WahlbezirkeClientImpl unitUnderTest;

    @Nested
    class GetWahlbezirke {

        @Test
        void should_returnWahlbezirke_when_wahltagIDExists() {
            val wahltagID = "wahltagID";
            val wahltag = LocalDate.now();

            val mockedWahlbezirkDTOList = List
                    .of(new WahlbezirkDTO().wahlbezirkID("wahlbezirkID").wahltag(wahltag).nummer("123").wahlbezirkart(WahlbezirkDTO.WahlbezirkartEnum.UWB));
            val mockedWahlbezirkModelList = wahlbezirkeClientMapper.fromListOfWahlbezirkDTOtoListOfWahlbezirkModel(mockedWahlbezirkDTOList);

            Mockito.when(wahlbezirkeControllerApi.getWahlbezirke(wahltagID)).thenReturn(mockedWahlbezirkDTOList);
            Mockito.when(wahlbezirkeClientMapper.fromListOfWahlbezirkDTOtoListOfWahlbezirkModel(mockedWahlbezirkDTOList)).thenReturn(mockedWahlbezirkModelList);

            val result = unitUnderTest.getWahlbezirke(wahltagID);

            Assertions.assertThat(result).isEqualTo(mockedWahlbezirkModelList);
        }

        @Test
        void should_rethrowWlsException_when_wlsExceptionIsThrownFromWahlbezirkeApi() {
            val wahltagID = "wahltagID";
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with wahlbezirke api failed");

            Mockito.doThrow(mockedWlsException).when(wahlbezirkeControllerApi).getWahlbezirke(wahltagID);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlbezirke(wahltagID)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwTechnischeWlsException_when_nonWlsExceptionIsThrownFromWahlbezirkeApi() {
            val wahltagID = "wahltagID";
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with wahlbezirke api failed");

            Mockito.doThrow(new RuntimeException("api call failed")).when(wahlbezirkeControllerApi).getWahlbezirke(wahltagID);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_BASISDATEN))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlbezirke(wahltagID)).isSameAs(mockedWlsException);
        }
    }
}
