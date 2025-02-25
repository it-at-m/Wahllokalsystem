package de.muenchen.oss.wahllokalsystem.adminservice.service.konfiguriertewahltage;

import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.konfigurierterwahltag.KonfigurierteWahltageService;
import de.muenchen.oss.wahllokalsystem.adminservice.service.konfigurierterwahltag.KonfigurierterWahltagValidator;
import de.muenchen.oss.wahllokalsystem.adminservice.service.konfigurierterwahltag.WahlenClient;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.KonfigurierterWahltagClient;
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
class KonfigurierteWahltageServiceTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    KonfigurierterWahltagClient konfigurierterWahltagClient;

    @Mock
    WahlenClient wahlenClient;

    @Mock
    KonfigurierterWahltagValidator konfigurierterWahltagValidator;

    @InjectMocks
    KonfigurierteWahltageService unitUnderTest;

    @Nested
    class GetKonfigurierteWahltage {

        @Test
        void should_returnKonfigurierteWahltage_when_callingGetKonfigurierteWahltage() {
            val wahlbezirkID = "wahlbezirkID";
            val mockedKonfigurierterWahltagModel = new KonfigurierterWahltagModel(LocalDate.now(), wahlbezirkID, true, "1");
            val mockedKonfigurierteWahltageList = List.of(mockedKonfigurierterWahltagModel);

            Mockito.when(konfigurierterWahltagClient.getKonfigurierteWahltage()).thenReturn(mockedKonfigurierteWahltageList);
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getKonfigurierteWahltage());

            Mockito.verify(konfigurierterWahltagClient).getKonfigurierteWahltage();
        }
    }

    @Nested
    class PostKonfigurierterWahltag {

        @Test
        void should_throwNoException_when_givenKonfigurierterWahltag() {
            val wahlbezirkID = "wahlbezirkID";
            val mockedKonfigurierterWahltagModel = new KonfigurierterWahltagModel(LocalDate.now(), wahlbezirkID, true, "1");

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postKonfigurierterWahltag(mockedKonfigurierterWahltagModel));

            Mockito.verify(konfigurierterWahltagValidator).validateModel(mockedKonfigurierterWahltagModel);
            Mockito.verify(konfigurierterWahltagClient).postKonfigurierterWahltag(mockedKonfigurierterWahltagModel);
        }

        @Test
        void should_throwTechnischeWlsException_when_exceptionIsThrownByClient() {
            val wahlbezirkID = "wahlbezirkID";
            val mockedKonfigurierterWahltagModel = new KonfigurierterWahltagModel(LocalDate.now(), wahlbezirkID, true, "1");

            val expectedException = TechnischeWlsException.withCode("").buildWithMessage("");
            Mockito.doThrow(expectedException).when(konfigurierterWahltagClient).postKonfigurierterWahltag(mockedKonfigurierterWahltagModel);
            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.postKonfigurierterWahltag(mockedKonfigurierterWahltagModel))
                    .isSameAs(expectedException);

            Mockito.verify(konfigurierterWahltagValidator).validateModel(mockedKonfigurierterWahltagModel);
        }

        @Test
        void should_resetWahlen_when_statusIsAktiv() {
            val wahlbezirkID = "wahlbezirkID";
            val mockedKonfigurierterWahltagModel = new KonfigurierterWahltagModel(LocalDate.now(), wahlbezirkID, true, "1");

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postKonfigurierterWahltag(mockedKonfigurierterWahltagModel));

            Mockito.verify(wahlenClient).resetWahlen();
        }

        @Test
        void should_notResetWahlen_when_statusIsInaktiv() {
            val wahlbezirkID = "wahlbezirkID";
            val mockedKonfigurierterWahltagModel = new KonfigurierterWahltagModel(LocalDate.now(), wahlbezirkID, false, "1");

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postKonfigurierterWahltag(mockedKonfigurierterWahltagModel));

            Mockito.verifyNoInteractions(wahlenClient);
        }

        @Test
        void should_throwTechnischeWlsException_when_exceptionIsThrownByApi() {
            val wahlbezirkID = "wahlbezirkID";
            val mockedWlsException = TechnischeWlsException.withCode("").buildWithMessage("");
            val mockedKonfigurierterWahltagModel = new KonfigurierterWahltagModel(LocalDate.now(), wahlbezirkID, true, "1");

            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_BASISDATEN)).thenReturn(mockedWlsException);
            Mockito.doThrow(mockedWlsException).when(wahlenClient).resetWahlen();
            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.postKonfigurierterWahltag(mockedKonfigurierterWahltagModel))
                    .isSameAs(mockedWlsException);
        }
    }
}
