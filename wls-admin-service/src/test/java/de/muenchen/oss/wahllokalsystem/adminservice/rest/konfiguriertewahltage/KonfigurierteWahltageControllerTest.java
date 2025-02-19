package de.muenchen.oss.wahllokalsystem.adminservice.rest.konfiguriertewahltage;

import de.muenchen.oss.wahllokalsystem.adminservice.client.infomanagement.KonfigurierterWahltagClientMapper;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.adminservice.rest.konfigurierterwahltag.KonfigurierteWahltageController;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.konfigurierterwahltag.KonfigurierteWahltageService;
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
    KonfigurierterWahltagClientMapper konfigurierterWahltagClientMapper;

    @InjectMocks
    KonfigurierteWahltageController unitUnderTest;

    @Nested
    class GetKonfigurierteWahltage {

        @Test
        void should_callService_when_controllerIsCalled() {

            val mockedServiceResponse = new KonfigurierterWahltagModel(LocalDate.now(), "wahltagID", true, "0");
            val mockedServiceResponseAsDTO = new KonfigurierterWahltagDTO();

            Mockito.when(konfigurierteWahltageService.getKonfigurierteWahltage()).thenReturn(List.of(mockedServiceResponse));
            Mockito.when(konfigurierterWahltagClientMapper.toDto(mockedServiceResponse)).thenReturn(mockedServiceResponseAsDTO);

            val result = unitUnderTest.getKonfigurierteWahltage();

            Mockito.verify(konfigurierteWahltageService).getKonfigurierteWahltage();

            Assertions.assertThat(result.get(0)).isEqualTo(mockedServiceResponseAsDTO);
        }
    }
}
