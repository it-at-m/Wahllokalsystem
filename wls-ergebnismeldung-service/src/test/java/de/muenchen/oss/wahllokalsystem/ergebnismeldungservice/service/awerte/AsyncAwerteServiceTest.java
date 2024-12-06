package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte;

import static org.mockito.Mockito.times;

import ch.qos.logback.classic.Level;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerteRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.LoggerExtension;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Base64;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AsyncAwerteServiceTest {

    @Mock
    AsyncProgress asyncProgress;

    @Mock
    AWerteRepository aWerteRepository;

    @Mock
    AWerteValidator aWerteValidator;

    @Mock
    AWerteModelMapper aWerteModelMapper;

    @Mock
    AWerteClient aWerteClient;

    @RegisterExtension
    public LoggerExtension loggerExtension = new LoggerExtension();

    @InjectMocks
    AsyncAWerteService unitUnderTest;

    @Nested
    class InitialiseAWerte {

        @Test
        void should_notFailAndSaveInDB_when_clientDataFound() {
            val wahlbezirkID1 = "wahlbezirkID1";
            val aWerteModelListFromClient1 = createListOfAWerteModels(wahlbezirkID1);
            val wahlbezirkID2 = "wahlbezirkID2";
            val aWerteModelListFromClient2 = createListOfAWerteModels(wahlbezirkID2);
            val wahlbezirkID3 = "wahlbezirkID3";
            val aWerteModelListFromClient3 = createListOfAWerteModels(wahlbezirkID3);
            val wahlbezirkIDs = List.of(wahlbezirkID1, wahlbezirkID2, wahlbezirkID3);
            Mockito.when(aWerteClient.getAWerte(wahlbezirkID1)).thenReturn(aWerteModelListFromClient1);
            Mockito.when(aWerteClient.getAWerte(wahlbezirkID2)).thenReturn(aWerteModelListFromClient2);
            Mockito.when(aWerteClient.getAWerte(wahlbezirkID3)).thenReturn(aWerteModelListFromClient3);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.initialiseAWerte(wahlbezirkIDs));
            Mockito.verify(asyncProgress, times(3)).setAWerteNext(Mockito.any());
            Mockito.verify(aWerteValidator, times(3)).validWahlbezirkIDParamOrThrow(Mockito.any());
            Mockito.verify(aWerteModelMapper, times(3)).fromListOfAWerteModeltoListOfAWerteEntity(Mockito.any());
            Mockito.verify(aWerteRepository, times(3)).saveAll(Mockito.any());

        }

        @Test
        void should_notFailAndSaveInDB_when_base64ClientDataFound() {
            val wahlbezirkID1 = Base64.getEncoder().encodeToString("WAHLBEZIRK-1410\",\"wahlterminId".getBytes());
            val aWerteModelListFromClient1 = createListOfAWerteModels(wahlbezirkID1);
            val wahlbezirkID2 = Base64.getEncoder().encodeToString("WAHLBEZIRK-1411\",\"wahlterminId".getBytes());
            val aWerteModelListFromClient2 = createListOfAWerteModels(wahlbezirkID2);
            val wahlbezirkID3 = Base64.getEncoder().encodeToString("WAHLBEZIRK-1412\",\"wahlterminId".getBytes());
            val aWerteModelListFromClient3 = createListOfAWerteModels(wahlbezirkID3);
            val wahlbezirkIDs = List.of(wahlbezirkID1, wahlbezirkID2, wahlbezirkID3);
            Mockito.when(aWerteClient.getAWerte(wahlbezirkID1)).thenReturn(aWerteModelListFromClient1);
            Mockito.when(aWerteClient.getAWerte(wahlbezirkID2)).thenReturn(aWerteModelListFromClient2);
            Mockito.when(aWerteClient.getAWerte(wahlbezirkID3)).thenReturn(aWerteModelListFromClient3);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.initialiseAWerte(wahlbezirkIDs));
            Mockito.verify(aWerteRepository, times(3)).saveAll(Mockito.any());
        }

        @Test
        void should_notFailButLogError_when_clientDataFoundAndRepoFails() {
            val wahlbezirkID1 = "wahlbezirkID1";
            val aWerteModelListFromClient1 = createListOfAWerteModels(wahlbezirkID1);
            val wahlbezirkID2 = "wahlbezirkID2";
            val aWerteModelListFromClient2 = createListOfAWerteModels(wahlbezirkID2);
            val wahlbezirkID3 = "wahlbezirkID3";
            val aWerteModelListFromClient3 = createListOfAWerteModels(wahlbezirkID3);
            val wahlbezirkIDs = List.of(wahlbezirkID1, wahlbezirkID2, wahlbezirkID3);
            Mockito.when(aWerteClient.getAWerte(wahlbezirkID1)).thenReturn(aWerteModelListFromClient1);
            Mockito.when(aWerteClient.getAWerte(wahlbezirkID2)).thenReturn(aWerteModelListFromClient2);
            Mockito.when(aWerteClient.getAWerte(wahlbezirkID3)).thenReturn(aWerteModelListFromClient3);
            Mockito.when(aWerteRepository.saveAll(Mockito.any())).thenThrow(new RuntimeException());

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.initialiseAWerte(wahlbezirkIDs));
            Mockito.verify(aWerteRepository, times(3)).saveAll(Mockito.any());
            Assertions.assertThat(loggerExtension.getLoggedEventsStream().filter(event -> event.getLevel() == Level.ERROR).count()).isEqualTo(3);
        }
    }

    private List<AWerteModel> createListOfAWerteModels(String wahlbezirkID) {
        val aWert1 = new AWerteModel(new BezirkUndWahlID("wahlID1", wahlbezirkID), 2, 3L);
        val aWert2 = new AWerteModel(new BezirkUndWahlID("wahlID2", wahlbezirkID), 4, 5L);
        val aWert3 = new AWerteModel(new BezirkUndWahlID("wahlID3", wahlbezirkID), 5, 6L);
        return List.of(aWert1, aWert2, aWert3);
    }

}
