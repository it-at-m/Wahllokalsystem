package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahltag;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahltagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahl;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.WahlRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahlart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WahlenServiceTest {

    @Mock
    WahlRepository wahlRepository;

    @Mock
    WahltagRepository wahltagRepository;

    @Mock
    WahlenValidator wahlenValidator;

    @Mock
    WahlModelMapper wahlModelMapper;

    @Mock
    WahlenClient wahlenClient;

    @InjectMocks
    WahlenService unitUnderTest;

    @Mock
    ExceptionFactory exceptionFactory;

    @Nested
    class GetWahlen {

        @Test
        void ifRepoDataFoundThanReturnsRepoDataAndMakesNoCallToRemoteClient() {
            val numberOfWahlenInRepo = 3;
            var searchingForWahltag = new Wahltag("wahltagID", LocalDate.now(), "beschreibung14", "1");
            Optional<Wahltag> mOp = Optional.of(searchingForWahltag);
            List<Wahl> mockedListOfEntities = createWahlEntities();
            List<WahlModel> mockedListOfModels = createWahlModels("");
            Mockito.doNothing().when(wahlenValidator).validWahlenCriteriaOrThrow("wahltagID");
            Mockito.doNothing().when(wahlenValidator).validateWahltagForSearchingWahltagID(mOp);

            Mockito.when(wahltagRepository.findById("wahltagID")).thenReturn(Optional.of(searchingForWahltag));
            Mockito.when(wahlRepository.countByWahltag(searchingForWahltag.getWahltag())).thenReturn(numberOfWahlenInRepo);
            Mockito.when(wahlRepository.findByWahltagOrderByReihenfolge(searchingForWahltag.getWahltag())).thenReturn(mockedListOfEntities);
            Mockito.when(wahlModelMapper.fromListOfWahlEntityToListOfWahlModel(mockedListOfEntities)).thenReturn(mockedListOfModels);
            val expectedResult = wahlModelMapper.fromListOfWahlEntityToListOfWahlModel(mockedListOfEntities);

            val result = unitUnderTest.getWahlen("wahltagID");
            Assertions.assertThatCode(() -> unitUnderTest.getWahlen("wahltagID")).doesNotThrowAnyException();
            Assertions.assertThat(result).isEqualTo(expectedResult);

            Mockito.verifyNoInteractions(wahlenClient);
        }

        @Test
        void ifRepoDataNotFoundThanReturnsRemoteClientData() {
            val numberOfWahlenInRepo = 0;
            var searchingForWahltag = new Wahltag("wahltagID", LocalDate.now(), "beschreibung15", "1");
            Optional<Wahltag> mOp = Optional.of(searchingForWahltag);
            List<Wahl> mockedListOfEntities = createWahlEntities();
            List<WahlModel> mockedListOfModelsIfClientCall = createWahlModels("clientPraefix");
            Mockito.doNothing().when(wahlenValidator).validWahlenCriteriaOrThrow("wahltagID");
            Mockito.doNothing().when(wahlenValidator).validateWahltagForSearchingWahltagID(mOp);

            Mockito.when(wahltagRepository.findById("wahltagID")).thenReturn(Optional.of(searchingForWahltag));
            Mockito.when(wahlRepository.countByWahltag(searchingForWahltag.getWahltag())).thenReturn(numberOfWahlenInRepo);
            Mockito.when(wahlRepository.findByWahltagOrderByReihenfolge(searchingForWahltag.getWahltag())).thenReturn(mockedListOfEntities);
            Mockito.when(wahlenClient.getWahlen(new WahltagWithNummer(searchingForWahltag.getWahltag(), searchingForWahltag.getNummer())))
                    .thenReturn(mockedListOfModelsIfClientCall);
            Mockito.when(wahlModelMapper.fromListOfWahlModeltoListOfWahlEntities(mockedListOfModelsIfClientCall)).thenReturn(mockedListOfEntities);
            Mockito.when(wahlModelMapper.fromListOfWahlEntityToListOfWahlModel(mockedListOfEntities)).thenReturn(mockedListOfModelsIfClientCall);

            val result = unitUnderTest.getWahlen("wahltagID");
            Assertions.assertThatCode(() -> unitUnderTest.getWahlen("wahltagID")).doesNotThrowAnyException();
            Assertions.assertThat(result).isEqualTo(mockedListOfModelsIfClientCall);
        }
    }

    @Nested
    class PostWahlen {

        @Test
        void dataSaved() {
            val wahltagID = "wahltagID";
            List<WahlModel> mockedListOfModels = createWahlModels("");
            List<Wahl> mockedListOfEntities = createWahlEntities();
            val wahlenToWrite = new WahlenWriteModel(wahltagID, mockedListOfModels);

            Mockito.when(wahlModelMapper.fromListOfWahlModeltoListOfWahlEntities(mockedListOfModels)).thenReturn(mockedListOfEntities);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.postWahlen(wahlenToWrite));
            Mockito.verify(wahlenValidator).validWahlenWriteModelOrThrow(new WahlenWriteModel(wahltagID, mockedListOfModels));
            Mockito.verify(wahlRepository).saveAll(mockedListOfEntities);
        }

        @Test
        void wlsExceptionWhenSavingFailed() {
            val wahltagID = "wahltagID";
            List<WahlModel> mockedListOfModels = createWahlModels("");
            List<Wahl> mockedListOfEntities = createWahlEntities();
            val wahlenToWrite = new WahlenWriteModel(wahltagID, mockedListOfModels);

            val mockedRepoSaveException = new RuntimeException("saving failed");
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(wahlModelMapper.fromListOfWahlModeltoListOfWahlEntities(mockedListOfModels)).thenReturn(mockedListOfEntities);
            Mockito.doThrow(mockedRepoSaveException).when(wahlRepository).saveAll(mockedListOfEntities);
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.CODE_POSTWAHLEN_UNSAVEABLE))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.postWahlen(wahlenToWrite)).isSameAs(mockedWlsException);
        }
    }

    @Nested
    class ResetWahlen {

        @Test
        void dataSuccessfullyReseted() {
            ArgumentCaptor<List<Wahl>> reqCaptor = ArgumentCaptor.forClass(List.class);

            val wahlenToReset = createWahlEntities();
            Mockito.when(wahlRepository.findAll()).thenReturn(wahlenToReset);
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.resetWahlen());

            val resetedWahlen = createWahlEntities().stream().map(this::resetWahl).toList();

            Mockito.verify(wahlRepository).saveAll(reqCaptor.capture());
            Assertions.assertThat(reqCaptor.getValue()).containsExactlyInAnyOrderElementsOf(resetedWahlen);
        }

        private Wahl resetWahl(Wahl wahl) {
            wahl.setFarbe(new Farbe(0, 0, 0));
            wahl.setReihenfolge(0);
            wahl.setWaehlerverzeichnisnummer(1);
            return wahl;
        }
    }

    private List<Wahl> createWahlEntities() {
        Wahl wahl1 = new Wahl();
        wahl1.setWahlID("wahlid1");
        wahl1.setName("wahl1");
        wahl1.setNummer("0");
        wahl1.setFarbe(new Farbe(1, 1, 1));
        wahl1.setWahlart(Wahlart.BAW);
        wahl1.setReihenfolge(1);
        wahl1.setWaehlerverzeichnisnummer(1);
        wahl1.setWahltag(LocalDate.now().plusMonths(1));

        Wahl wahl2 = new Wahl();
        wahl2.setWahlID("wahlid2");
        wahl2.setName("wahl2");
        wahl2.setNummer("1");
        wahl2.setFarbe(new Farbe(2, 2, 2));
        wahl2.setWahlart(Wahlart.LTW);
        wahl2.setReihenfolge(2);
        wahl2.setWaehlerverzeichnisnummer(2);
        wahl2.setWahltag(LocalDate.now().plusMonths(2));

        Wahl wahl3 = new Wahl();
        wahl3.setWahlID("wahlid3");
        wahl3.setName("wahl3");
        wahl3.setNummer("2");
        wahl3.setFarbe(new Farbe(3, 3, 3));
        wahl3.setWahlart(Wahlart.EUW);
        wahl3.setReihenfolge(3);
        wahl3.setWaehlerverzeichnisnummer(3);
        wahl3.setWahltag(LocalDate.now().plusMonths(3));
        List<Wahl> lw = new ArrayList<>();
        lw.add(wahl1);
        lw.add(wahl2);
        lw.add(wahl3);

        return lw;
    }

    private List<WahlModel> createWahlModels(final String clientPraefix) {
        WahlModel wahl1 = new WahlModel("wahlid1", clientPraefix + "wahl1", 1L,
                1L, LocalDate.now().plusMonths(1),
                Wahlart.BAW, new Farbe(1, 1, 1), "0");
        WahlModel wahl2 = new WahlModel("wahlid2", clientPraefix + "wahl2", 2L,
                2L, LocalDate.now().plusMonths(2),
                Wahlart.LTW, new Farbe(2, 2, 2), "1");
        WahlModel wahl3 = new WahlModel("wahlid3", clientPraefix + "wahl3", 3L,
                3L, LocalDate.now().plusMonths(3),
                Wahlart.LTW, new Farbe(3, 3, 3), "2");
        List<WahlModel> lw = new ArrayList<>();
        lw.add(wahl1);
        lw.add(wahl2);
        lw.add(wahl3);
        return lw;
    }

}
