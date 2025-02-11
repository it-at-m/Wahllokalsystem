package de.muenchen.oss.wahllokalsystem.adminservice.client.basisdaten;

import de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.client.WahltageControllerApi;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.model.WahltagDTO;
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
class WahltageClientImplTest {

    @Mock
    WahltageControllerApi wahltageControllerApi;

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    WahltagClientMapper wahltagClientMapper;

    @InjectMocks
    WahltageClientImpl unitUnderTest;

    @Nested
    class GetWahltage {

        @Test
        void should_returnWahltage_when_wahltageExists() {
            val wahltag = LocalDate.now();
            val mockedWahltageDTOList = List.of(
                    new WahltagDTO().wahltagID("wahltagID1").wahltag(wahltag).beschreibung("beschreibung").nummer("1"),
                    new WahltagDTO().wahltagID("wahltagID2").wahltag(wahltag).beschreibung("beschreibung").nummer("2"),
                    new WahltagDTO().wahltagID("wahltagID3").wahltag(wahltag).beschreibung("beschreibung").nummer("3"));
            val mockedWahltageModelList = wahltagClientMapper.fromListOfWahltagDTOtoListOfWahltagModel(mockedWahltageDTOList);

            Mockito.when(wahltageControllerApi.getWahltage()).thenReturn(mockedWahltageDTOList);
            Mockito.when(wahltagClientMapper.fromListOfWahltagDTOtoListOfWahltagModel(mockedWahltageDTOList)).thenReturn(mockedWahltageModelList);

            val result = unitUnderTest.getWahltage();

            Assertions.assertThat(result).isEqualTo(mockedWahltageModelList);
        }

        @Test
        void should_rethrowWlsException_when_wlsExceptionIsThrownFromWahltageApi() {
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with wahltage api failed");

            Mockito.doThrow(mockedWlsException).when(wahltageControllerApi).getWahltage();

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahltage()).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwTechnischeWlsException_when_nonWlsExceptionIsThrownFromWahltageApi() {
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with wahltage api failed");

            Mockito.doThrow(new RuntimeException("api call failed")).when(wahltageControllerApi).getWahltage();
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_BASISDATEN))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahltage()).isSameAs(mockedWlsException);
        }
    }
}
