package de.muenchen.oss.wahllokalsystem.adminservice.client.infomanagement;

import de.muenchen.oss.wahllokalsystem.adminservice.eai.infomanagement.client.KonfigurierterWahltagControllerApi;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDate;
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
class KonfigurierterWahltagClientImplTest {

    @Mock
    KonfigurierterWahltagControllerApi konfigurierterWahltagControllerApi;

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    KonfigurierterWahltagClientMapper konfigurierterWahltagClientMapper;

    @InjectMocks
    KonfigurierterWahltagClientImpl unitUnderTest;

    @Nested
    class PostKonfigurierterWahltag {

        @Test
        void should_verifySetKonfigurierterWahltagApiCall_when_konfigurierterWahltagModelIsGiven() {
            val nowDate = LocalDate.now();
            val konfigurierterWahltagModel = new KonfigurierterWahltagModel(nowDate, "1-2-3", true, "123");
            val mockedKonfigurierterWahltagDTO = new KonfigurierterWahltagDTO().wahltag(nowDate).wahltagID("1-2-3").wahltagStatus(
                    KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV).nummer("123");

            Mockito.when(konfigurierterWahltagClientMapper.toClientDto(konfigurierterWahltagModel)).thenReturn(mockedKonfigurierterWahltagDTO);

            unitUnderTest.postKonfigurierterWahltag(konfigurierterWahltagModel);

            Mockito.verify(konfigurierterWahltagControllerApi).setKonfigurierterWahltag(mockedKonfigurierterWahltagDTO);
        }

        @Test
        void should_rethrowWlsException_when_wlsExceptionIsThrownFromKonfigurierterWahltagApi() {
            val nowDate = LocalDate.now();
            val konfigurierterWahltagModel = new KonfigurierterWahltagModel(nowDate, "1-2-3", true, "123");
            val mockedKonfigurierterWahltagDTO = new KonfigurierterWahltagDTO().wahltag(nowDate).wahltagID("1-2-3").wahltagStatus(
                    KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV).nummer("123");

            Mockito.when(konfigurierterWahltagClientMapper.toClientDto(konfigurierterWahltagModel)).thenReturn(mockedKonfigurierterWahltagDTO);

            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with KonfigurierterWahltagApi api failed");

            Mockito.doThrow(mockedWlsException).when(konfigurierterWahltagControllerApi).setKonfigurierterWahltag(mockedKonfigurierterWahltagDTO);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.postKonfigurierterWahltag(konfigurierterWahltagModel)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwTechnischeWlsException_when_nonWlsExceptionIsThrownFromAKonfigurierterWahltagApi() {
            val nowDate = LocalDate.now();
            val konfigurierterWahltagModel = new KonfigurierterWahltagModel(nowDate, "1-2-3", true, "123");
            val mockedKonfigurierterWahltagDTO = new KonfigurierterWahltagDTO().wahltag(nowDate).wahltagID("1-2-3").wahltagStatus(
                    KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV).nummer("123");
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with KonfigurierterWahltagApi api failed");

            Mockito.when(konfigurierterWahltagClientMapper.toClientDto(konfigurierterWahltagModel)).thenReturn(mockedKonfigurierterWahltagDTO);
            Mockito.doThrow(new RuntimeException("api call failed")).when(konfigurierterWahltagControllerApi)
                    .setKonfigurierterWahltag(mockedKonfigurierterWahltagDTO);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_INFOMANAGEMENT))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.postKonfigurierterWahltag(konfigurierterWahltagModel)).isSameAs(mockedWlsException);
        }
    }
}
