package de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten;

import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDate;
import java.util.Collections;
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
class WahltermindatenServiceTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    WahltermindatenValidator wahltermindatenValidator;

    @Mock
    WahltermindatenClient wahltermindatenClient;

    @Mock
    WahltageClient wahltageClient;

    @Mock
    WahlbezirkeClient wahlbezirkeClient;

    @Mock
    KonfigurierterWahltagClient konfigurierterWahltagClient;

    @InjectMocks
    WahltermindatenService unitUnderTest;

    @Nested
    class LoadWahltermindaten {

        @Test
        void should_throwTechnischeWlsException_when_wahltagIsNotFoundForWahltagID() {
            val wahltagID = "wahltagID";

            val mockedTechnischeWlsException = TechnischeWlsException.withCode("000").buildWithMessage("Wahltag not found for wahltagID");

            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.INVALID_ARGUMENT)).thenReturn(mockedTechnischeWlsException);
            Mockito.when(wahltageClient.getWahltage()).thenReturn(Collections.emptyList());
            Mockito.when(wahlbezirkeClient.getWahlbezirke(wahltagID)).thenReturn(Collections.emptyList());

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.loadWahltermindaten(wahltagID))
                    .isSameAs(mockedTechnischeWlsException);
        }

        @Test
        void should_postKonfigurierterWahltag_when_aWerteAreInitialised() {
            val wahltagID = "wahltagID";
            val mockedWahltagModel = new WahltagModel(wahltagID, LocalDate.now(), "beschreibung", "nummer");
            val mockedWahltagList = List.of(mockedWahltagModel);
            val mockedWahlbezirkModel = new WahlbezirkModel("wahlbezirkID", WahlbezirkArtModel.UWB, "nummer",
                    LocalDate.now(), "wahlnummer", "wahlID");
            val mockedWahbezirkList = List.of(mockedWahlbezirkModel);

            Mockito.when(wahltageClient.getWahltage()).thenReturn(mockedWahltagList);
            Mockito.when(wahlbezirkeClient.getWahlbezirke(wahltagID)).thenReturn(mockedWahbezirkList);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.loadWahltermindaten(wahltagID));
            Mockito.verify(konfigurierterWahltagClient)
                    .postKonfigurierterWahltag(new KonfigurierterWahltagModel(mockedWahltagModel.wahltag(), mockedWahltagModel.wahltagID(),
                            false, mockedWahltagModel.nummer()));
        }

        @Test
        void should_doRollbackAndDeleteWahltermindaten_when_postKonfigurierterWahltagFails() {
            val wahltagID = "wahltagID";
            val mockedWahltag = new WahltagModel(wahltagID, LocalDate.now(), "beschreibung", "nummer");
            val mockedWahltagList = List.of(mockedWahltag);
            Mockito.when(wahltageClient.getWahltage()).thenReturn(mockedWahltagList);
            val mockedException = Mockito.mock(RuntimeException.class);
            Mockito.when(wahlbezirkeClient.getWahlbezirke(wahltagID)).thenReturn(Collections.emptyList());
            Mockito.doThrow(mockedException)
                    .when(konfigurierterWahltagClient)
                    .postKonfigurierterWahltag(new KonfigurierterWahltagModel(mockedWahltag.wahltag(), mockedWahltag.wahltagID(),
                            false, mockedWahltag.nummer()));

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.loadWahltermindaten(wahltagID)).isSameAs(mockedException);
            Mockito.verify(wahltermindatenClient).deleteWahltermindaten(wahltagID);
        }
    }

    @Nested
    class DeleteWahltermindaten {

        @Test
        void should_deleteWahltermindaten_when_allClientCallsAreSuccesful() {
            val wahltagID = "wahltagID";

            Mockito.doNothing().when(wahltermindatenClient).deleteWahltermindaten(wahltagID);
            Mockito.doNothing().when(konfigurierterWahltagClient).deleteKonfigurierterWahltag(wahltagID);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.deleteWahltermindaten(wahltagID));
        }

        @Test
        void should_throwRuntimeException_when_wahltermindatenClientFails() {
            val wahltagID = "wahltagID";

            Mockito.doThrow(new RuntimeException()).when(wahltermindatenClient).deleteWahltermindaten(wahltagID);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.deleteWahltermindaten(wahltagID)).isInstanceOf(RuntimeException.class);
        }

        @Test
        void should_throwRuntimeException_when_konfigurierterWahltagClientFails() {
            val wahltagID = "wahltagID";

            Mockito.doThrow(new RuntimeException()).when(konfigurierterWahltagClient).deleteKonfigurierterWahltag(wahltagID);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.deleteWahltermindaten(wahltagID)).isInstanceOf(RuntimeException.class);
        }
    }
}
