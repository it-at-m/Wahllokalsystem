package de.muenchen.oss.wahllokalsystem.adminservice.client.infomanagement;

import de.muenchen.oss.wahllokalsystem.adminservice.eai.infomanagement.client.KonfigurierterWahltagControllerApi;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.KonfigurierterWahltagModel;
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
class KonfigurierteWahltageClientImplTest {

    @Mock
    KonfigurierterWahltagControllerApi konfigurierterWahltagControllerApi;

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    KonfigurierterWahltagClientMapper konfigurierterWahltagClientMapper;

    @InjectMocks
    KonfigurierteWahltageClientImpl unitUnderTest;

    @Nested
    class GetKonfigurierteWahltage {

        @Test
        void should_callApi_when_konfigurierteWahltageIsCalled() throws Exception {
            val nowDate = LocalDate.now();
            val konfigurierterWahltagModel = new KonfigurierterWahltagModel(nowDate, "1-2-3", true, "123");
            val mockedKonfigurierterWahltagDTO = new KonfigurierterWahltagDTO().wahltag(nowDate).wahltagID("1-2-3").wahltagStatus(
                    KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV).nummer("123");

            Mockito.when(konfigurierterWahltagControllerApi.getKonfigurierteWahltage()).thenReturn(List.of(mockedKonfigurierterWahltagDTO));
            Mockito.when(konfigurierterWahltagClientMapper.toModel(mockedKonfigurierterWahltagDTO)).thenReturn(konfigurierterWahltagModel);

            unitUnderTest.getKonfigurierteWahltage();

            Mockito.verify(konfigurierterWahltagControllerApi).getKonfigurierteWahltage();
        }

        @Test
        void should_throwTechnischeWlsException_when_exceptionIsThrownByApi() throws Exception {
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("Communication with KonfigurierteWahltageApi failed");

            Mockito.doThrow(mockedWlsException).when(konfigurierterWahltagControllerApi).getKonfigurierteWahltage();
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_INFOMANAGEMENT))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getKonfigurierteWahltage()).isSameAs(mockedWlsException);
        }

        @Test
        void should_returnNull_when_apiReturnsNull() throws Exception {
            Mockito.when(konfigurierterWahltagControllerApi.getKonfigurierteWahltage()).thenReturn(null);

            Assertions.assertThat(unitUnderTest.getKonfigurierteWahltage()).isNull();
        }
    }
}
