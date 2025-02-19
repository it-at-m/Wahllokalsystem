package de.muenchen.oss.wahllokalsystem.adminservice.service.konfiguriertewahltage;

import de.muenchen.oss.wahllokalsystem.adminservice.service.common.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.konfigurierterwahltag.KonfigurierteWahltageClient;
import de.muenchen.oss.wahllokalsystem.adminservice.service.konfigurierterwahltag.KonfigurierteWahltageService;
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
class KonfigurierteWahltageServiceTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    KonfigurierteWahltageClient konfigurierteWahltageClient;

    @InjectMocks
    KonfigurierteWahltageService unitUnderTest;

    @Nested
    class GetKonfigurierteWahltage {

        @Test
        void should_returnKonfigurierteWahltage_when_callingGetKonfigurierteWahltage() {
            val wahlbezirkID = "wahlbezirkID";
            val mockedKonfigurierterWahltagModel = new KonfigurierterWahltagModel(LocalDate.now(), wahlbezirkID, true, "1");
            val mockedKonfigurierteWahltageList = List.of(mockedKonfigurierterWahltagModel);

            Mockito.when(konfigurierteWahltageClient.getKonfigurierteWahltage()).thenReturn(mockedKonfigurierteWahltageList);
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getKonfigurierteWahltage());

            Mockito.verify(konfigurierteWahltageClient).getKonfigurierteWahltage();
        }
    }
}
