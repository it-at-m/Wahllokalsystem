package de.muenchen.oss.wahllokalsystem.adminservice.client.infomanagement;

import de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.client.WahlenControllerApi;
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
class KonfigurierterWahltagClientImplTest {

    @Mock
    KonfigurierterWahltagControllerApi konfigurierterWahltagControllerApi;

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    KonfigurierterWahltagClientMapper konfigurierterWahltagClientMapper;

    @Mock
    WahlenControllerApi wahlenControllerApi;

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

            Mockito.when(konfigurierterWahltagClientMapper.toDto(konfigurierterWahltagModel)).thenReturn(mockedKonfigurierterWahltagDTO);

            unitUnderTest.postKonfigurierterWahltag(konfigurierterWahltagModel);

            Mockito.verify(konfigurierterWahltagControllerApi).setKonfigurierterWahltag(mockedKonfigurierterWahltagDTO);
        }

        @Test
        void should_rethrowWlsException_when_wlsExceptionIsThrownFromKonfigurierterWahltagApi() {
            val nowDate = LocalDate.now();
            val konfigurierterWahltagModel = new KonfigurierterWahltagModel(nowDate, "1-2-3", true, "123");
            val mockedKonfigurierterWahltagDTO = new KonfigurierterWahltagDTO().wahltag(nowDate).wahltagID("1-2-3").wahltagStatus(
                    KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV).nummer("123");

            Mockito.when(konfigurierterWahltagClientMapper.toDto(konfigurierterWahltagModel)).thenReturn(mockedKonfigurierterWahltagDTO);

            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with konfigurierter wahltag api failed");

            Mockito.doThrow(mockedWlsException).when(konfigurierterWahltagControllerApi).setKonfigurierterWahltag(mockedKonfigurierterWahltagDTO);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.postKonfigurierterWahltag(konfigurierterWahltagModel)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwTechnischeWlsException_when_nonWlsExceptionIsThrownFromAKonfigurierterWahltagApi() {
            val nowDate = LocalDate.now();
            val konfigurierterWahltagModel = new KonfigurierterWahltagModel(nowDate, "1-2-3", true, "123");
            val mockedKonfigurierterWahltagDTO = new KonfigurierterWahltagDTO().wahltag(nowDate).wahltagID("1-2-3").wahltagStatus(
                    KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV).nummer("123");
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with konfigurierter wahltag api failed");

            Mockito.when(konfigurierterWahltagClientMapper.toDto(konfigurierterWahltagModel)).thenReturn(mockedKonfigurierterWahltagDTO);
            Mockito.doThrow(new RuntimeException("api call failed")).when(konfigurierterWahltagControllerApi)
                    .setKonfigurierterWahltag(mockedKonfigurierterWahltagDTO);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_INFOMANAGEMENT))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.postKonfigurierterWahltag(konfigurierterWahltagModel)).isSameAs(mockedWlsException);
        }
    }

    @Nested
    class GetKonfigurierteWahltage {

        @Test
        void should_callApi_when_konfigurierteWahltageIsCalled() throws Exception {
            val nowDate = LocalDate.now();
            val mockedKonfigurierterWahltagModel = new KonfigurierterWahltagModel(nowDate, "1-2-3", true, "123");
            val mockedKonfigurierterWahltagDTO = new KonfigurierterWahltagDTO().wahltag(nowDate).wahltagID("1-2-3").wahltagStatus(
                    KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV).nummer("123");

            Mockito.when(konfigurierterWahltagControllerApi.getKonfigurierteWahltage()).thenReturn(List.of(mockedKonfigurierterWahltagDTO));
            Mockito.when(konfigurierterWahltagClientMapper.toModel(mockedKonfigurierterWahltagDTO)).thenReturn(mockedKonfigurierterWahltagModel);

            val result = unitUnderTest.getKonfigurierteWahltage();

            Assertions.assertThat(result).isEqualTo(List.of(mockedKonfigurierterWahltagModel));
        }

        @Test
        void should_rethrowWlsException_when_wlsExceptionIsThrownByApi() throws Exception {
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("Communication with KonfigurierteWahltageApi failed");

            Mockito.doThrow(mockedWlsException).when(konfigurierterWahltagControllerApi).getKonfigurierteWahltage();

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getKonfigurierteWahltage()).isSameAs(mockedWlsException);
        }

        @Test
        void should_returnEmptyList_when_apiReturnsNull() throws Exception {
            Mockito.when(konfigurierterWahltagControllerApi.getKonfigurierteWahltage()).thenReturn(null);

            Assertions.assertThat(unitUnderTest.getKonfigurierteWahltage()).isEmpty();
        }

        @Test
        void should_rethrowTechnischeWlsException_when_nonWlsExceptionIsThrownFromKonfigurierterWahltagControllerApi() {
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with wahlen api failed");

            Mockito.doThrow(new RuntimeException("api call failed")).when(konfigurierterWahltagControllerApi).getKonfigurierteWahltage();
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_INFOMANAGEMENT))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getKonfigurierteWahltage()).isSameAs(mockedWlsException);
        }
    }

    @Nested
    class DeleteKonfigurierterWahltag {
        @Test
        void should_verifyDeleteKonfigurierterWahltagApiCall_when_wahltagIDIsGiven() {
            val wahltagID = "wahltagID";

            unitUnderTest.deleteKonfigurierterWahltag(wahltagID);

            Mockito.verify(konfigurierterWahltagControllerApi).deleteKonfigurierterWahltag(wahltagID);
        }

        @Test
        void should_rethrowWlsException_when_wlsExceptionIsThrownFromKonfigurierterWahltagApi() {
            val wahltagID = "wahltagID";
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with konfigurierter wahltag api failed");

            Mockito.doThrow(mockedWlsException).when(konfigurierterWahltagControllerApi).deleteKonfigurierterWahltag(wahltagID);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.deleteKonfigurierterWahltag(wahltagID)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwTechnischeWlsException_when_nonWlsExceptionIsThrownFromKonfigurierterWahltagApi() {
            val wahltagID = "wahltagID";
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with konfigurierter wahltag api failed");

            Mockito.doThrow(new RuntimeException("api call failed")).when(konfigurierterWahltagControllerApi).deleteKonfigurierterWahltag(wahltagID);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_INFOMANAGEMENT))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.deleteKonfigurierterWahltag(wahltagID)).isSameAs(mockedWlsException);
        }
    }
}
