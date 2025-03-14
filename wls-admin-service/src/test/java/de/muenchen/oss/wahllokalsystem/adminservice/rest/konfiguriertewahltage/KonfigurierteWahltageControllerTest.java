package de.muenchen.oss.wahllokalsystem.adminservice.rest.konfiguriertewahltage;

import de.muenchen.oss.wahllokalsystem.adminservice.service.common.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.konfiguriertewahltage.KonfigurierteWahltageService;
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
class KonfigurierteWahltageControllerTest {

    @Mock
    KonfigurierteWahltageService konfigurierteWahltageService;

    @Mock
    KonfigurierterWahltagDTOMapper konfigurierterWahltagDTOMapper;

    @InjectMocks
    KonfigurierteWahltageController unitUnderTest;

    @Nested
    class GetKonfigurierteWahltage {

        @Test
        void should_callService_when_controllerIsCalled() {
            val mockedServiceResponse = new KonfigurierterWahltagModel(LocalDate.now(), "wahltagID", true, "0");
            val mockedServiceResponseAsDTO = new KonfigurierterWahltagDTO(LocalDate.now(), "wahltagID", WahltagStatusDTO.AKTIV, "0");

            Mockito.when(konfigurierteWahltageService.getKonfigurierteWahltage()).thenReturn(List.of(mockedServiceResponse));
            Mockito.when(konfigurierterWahltagDTOMapper.toDTO(mockedServiceResponse)).thenReturn(mockedServiceResponseAsDTO);

            val result = unitUnderTest.getKonfigurierteWahltage();

            Assertions.assertThat(result.getBody()).isEqualTo(List.of(mockedServiceResponseAsDTO));
        }
    }

    @Nested
    class PostKonfigurierterWahltag {

        @Test
        void should_callServiceWithModel_when_calledWithData() {
            val date = LocalDate.now();
            val wahltagID = "wahltagID";
            val nummer = "0";

            val konfigurierterWahltagDTO = new KonfigurierterWahltagDTO(date, wahltagID, WahltagStatusDTO.AKTIV, nummer);

            val mockedKonfigurierterWahltagModel = new KonfigurierterWahltagModel(date, wahltagID, true, nummer);
            Mockito.when(konfigurierterWahltagDTOMapper.toModel(konfigurierterWahltagDTO)).thenReturn(mockedKonfigurierterWahltagModel);

            unitUnderTest.postKonfigurierterWahltag(konfigurierterWahltagDTO);

            Mockito.verify(konfigurierteWahltageService).postKonfigurierterWahltag(mockedKonfigurierterWahltagModel);
        }
    }
}
