package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltermindaten;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.Referendumvorlagen;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.ReferendumvorlagenRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.Wahlart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.Wahlvorschlaege;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.WahlvorschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.WahltagWithNummer;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.BasisdatenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen.ReferendumvorlagenClient;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen.ReferendumvorlagenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen.ReferendumvorlagenModelMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke.WahlbezirkModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlag.WahlvorschlaegeClient;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlag.WahlvorschlaegeModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlag.WahlvorschlaegeModelMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AsyncWahltermindatenServiceTest {

    @Mock
    AsyncProgress asyncProgress;

    @Mock
    WahlvorschlaegeClient wahlvorschlaegeClient;
    @Mock
    ReferendumvorlagenClient referendumvorlagenClient;

    @Mock
    WahlvorschlaegeRepository wahlvorschlaegeRepository;
    @Mock
    ReferendumvorlagenRepository referendumvorlagenRepository;

    @Mock
    WahlvorschlaegeModelMapper wahlvorschlaegeModelMapper;
    @Mock
    ReferendumvorlagenModelMapper referendumvorlagenModelMapper;

    @InjectMocks
    AsyncWahltermindatenService unitUnderTest;

    @Nested
    class InitVorlagenAndVorschlaege {

        @Test
        void should_resetProgress_when_methodIsCalled() {
            val wahltagDate = LocalDate.now();
            val wahltagNummer = "wahltagNummer";
            val basisdatenModel = createEmptyBasisdatenModel();

            unitUnderTest.initVorlagenAndVorschlaege(new WahltagWithNummer(wahltagDate, wahltagNummer), basisdatenModel);

            Mockito.verify(asyncProgress).reset(wahltagDate, wahltagNummer);
        }

        @Test
        void should_persistWahlvorschlaege_when_basisdatenAreDelivered() {
            val wahltagDate = LocalDate.now();
            val wahltagNummer = "wahltagNummer";
            val basisdatenModel = createBasisdatenModelWith2WahlenAnd2WahlbezirkeForEachWahl(wahltagDate, Wahlart.BTW);

            val mockedWahlvorschlaegeClientResponse = WahlvorschlaegeModel.builder().build();
            val mockedWahlvorschlaegeModelMappedAsEntity = new Wahlvorschlaege();

            Mockito.when(wahlvorschlaegeClient.getWahlvorschlaege(any())).thenReturn(mockedWahlvorschlaegeClientResponse);
            Mockito.when(wahlvorschlaegeModelMapper.toEntity(mockedWahlvorschlaegeClientResponse)).thenReturn(mockedWahlvorschlaegeModelMappedAsEntity);

            unitUnderTest.initVorlagenAndVorschlaege(new WahltagWithNummer(wahltagDate, wahltagNummer), basisdatenModel);

            Mockito.verify(wahlvorschlaegeRepository, times(4)).save(mockedWahlvorschlaegeModelMappedAsEntity);
            Mockito.verify(asyncProgress, times(4)).incWahlvorschlaegeFinished();
            Mockito.verify(asyncProgress, times(4)).setWahlvorschlaegeNext(any());
        }

        @Test
        void should_increaseWahlvorstandProgressEven_when_savingFailed() {
            val wahltagDate = LocalDate.now();
            val wahltagNummer = "wahltagNummer";
            val basisdatenModel = createBasisdatenModelWith2WahlenAnd2WahlbezirkeForEachWahl(wahltagDate, Wahlart.BTW);

            val mockedWahlvorschlaegeClientResponse = WahlvorschlaegeModel.builder().build();
            val mockedWahlvorschlaegeModelMappedAsEntity = new Wahlvorschlaege();

            Mockito.when(wahlvorschlaegeClient.getWahlvorschlaege(any())).thenReturn(mockedWahlvorschlaegeClientResponse);
            Mockito.when(wahlvorschlaegeModelMapper.toEntity(mockedWahlvorschlaegeClientResponse)).thenReturn(mockedWahlvorschlaegeModelMappedAsEntity);
            Mockito.doThrow(new RuntimeException("saving failed")).when(wahlvorschlaegeRepository).save(mockedWahlvorschlaegeModelMappedAsEntity);

            unitUnderTest.initVorlagenAndVorschlaege(new WahltagWithNummer(wahltagDate, wahltagNummer), basisdatenModel);

            Mockito.verify(asyncProgress, times(4)).incWahlvorschlaegeFinished();
            Mockito.verify(asyncProgress, times(4)).setWahlvorschlaegeNext(any());
        }

        @Test
        void should_increaseWahlvorstandProgressEven_when_loadingWahlvorschlaegeFromClientFailed() {
            val wahltagDate = LocalDate.now();
            val wahltagNummer = "wahltagNummer";
            val basisdatenModel = createBasisdatenModelWith2WahlenAnd2WahlbezirkeForEachWahl(wahltagDate, Wahlart.BTW);

            Mockito.doThrow(new RuntimeException("getting data from client failed")).when(wahlvorschlaegeClient).getWahlvorschlaege(any());

            unitUnderTest.initVorlagenAndVorschlaege(new WahltagWithNummer(wahltagDate, wahltagNummer), basisdatenModel);

            Mockito.verify(asyncProgress, times(4)).incWahlvorschlaegeFinished();
            Mockito.verify(asyncProgress, times(4)).setWahlvorschlaegeNext(any());
        }

        @Test
        void should_persistReferendumvorlagen_when_basisdatenAreDelivered() {
            val wahltagDate = LocalDate.now();
            val wahltagNummer = "wahltagNummer";
            val basisdatenModel = createBasisdatenModelWith2WahlenAnd2WahlbezirkeForEachWahl(wahltagDate, Wahlart.BEB);

            val mockedReferendumvorlagenClientResponse = createEmptyReferendumvorlagenModel();
            val mockedReferendumvorlagenModelMappedAsEntity = new Referendumvorlagen();

            Mockito.when(referendumvorlagenClient.getReferendumvorlagen(any())).thenReturn(mockedReferendumvorlagenClientResponse);
            Mockito.when(referendumvorlagenModelMapper.toEntity(eq(mockedReferendumvorlagenClientResponse), any()))
                    .thenReturn(mockedReferendumvorlagenModelMappedAsEntity);

            unitUnderTest.initVorlagenAndVorschlaege(new WahltagWithNummer(wahltagDate, wahltagNummer), basisdatenModel);

            Mockito.verify(referendumvorlagenRepository, times(4)).save(mockedReferendumvorlagenModelMappedAsEntity);
            Mockito.verify(asyncProgress, times(4)).incReferendumVorlagenFinished();
            Mockito.verify(asyncProgress, times(4)).setReferendumVorlagenNext(any());
        }

        @Test
        void should_increaseReferendumvorlagenProgressEven_when_savingFailed() {
            val wahltagDate = LocalDate.now();
            val wahltagNummer = "wahltagNummer";
            val basisdatenModel = createBasisdatenModelWith2WahlenAnd2WahlbezirkeForEachWahl(wahltagDate, Wahlart.BEB);

            val mockedReferendumvorlagenClientResponse = createEmptyReferendumvorlagenModel();
            val mockedReferendumvorlagenModelMappedAsEntity = new Referendumvorlagen();

            Mockito.when(referendumvorlagenClient.getReferendumvorlagen(any())).thenReturn(mockedReferendumvorlagenClientResponse);
            Mockito.when(referendumvorlagenModelMapper.toEntity(eq(mockedReferendumvorlagenClientResponse), any()))
                    .thenReturn(mockedReferendumvorlagenModelMappedAsEntity);
            Mockito.doThrow(new RuntimeException("saving failed")).when(referendumvorlagenRepository).save(any());

            unitUnderTest.initVorlagenAndVorschlaege(new WahltagWithNummer(wahltagDate, wahltagNummer), basisdatenModel);

            Mockito.verify(asyncProgress, times(4)).incReferendumVorlagenFinished();
            Mockito.verify(asyncProgress, times(4)).setReferendumVorlagenNext(any());
        }

        @Test
        void should_increaseReferendumvorlagenProgressEven_when_loadingReferendumvorlagenFromClientFailed() {
            val wahltagDate = LocalDate.now();
            val wahltagNummer = "wahltagNummer";
            val basisdatenModel = createBasisdatenModelWith2WahlenAnd2WahlbezirkeForEachWahl(wahltagDate, Wahlart.BEB);

            Mockito.doThrow(new RuntimeException("getting data from client failed")).when(referendumvorlagenClient).getReferendumvorlagen(any());

            unitUnderTest.initVorlagenAndVorschlaege(new WahltagWithNummer(wahltagDate, wahltagNummer), basisdatenModel);

            Mockito.verify(asyncProgress, times(4)).incReferendumVorlagenFinished();
            Mockito.verify(asyncProgress, times(4)).setReferendumVorlagenNext(any());
        }

        @Test
        void should_propagateAuthentication_when_repositioriesAreCalled() {

        }

        private ReferendumvorlagenModel createEmptyReferendumvorlagenModel() {
            return new ReferendumvorlagenModel("sgzid", Collections.emptySet());
        }

        private BasisdatenModel createEmptyBasisdatenModel() {
            return new BasisdatenModel(Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
        }

        private BasisdatenModel createBasisdatenModelWith2WahlenAnd2WahlbezirkeForEachWahl(final LocalDate wahltagDate, final Wahlart wahlArt) {
            val wahlenModels = Set.of(
                    new WahlModel("wahlID1", "wahl1", 1L, 1L, wahltagDate, wahlArt, null, "1"),
                    new WahlModel("wahlID2", "wahl2", 2L, 2L, wahltagDate, wahlArt, null, "2"));

            val wahlbezirkeModels = Set.of(
                    new WahlbezirkModel("wahlbezirkID1", WahlbezirkArtModel.UWB, "1", wahltagDate, "1", "wahlID1"),
                    new WahlbezirkModel("wahlbezirkID2", WahlbezirkArtModel.UWB, "2", wahltagDate, "1", "wahlID1"),
                    new WahlbezirkModel("wahlbezirkID3", WahlbezirkArtModel.UWB, "3", wahltagDate, "2", "wahlID2"),
                    new WahlbezirkModel("wahlbezirkID4", WahlbezirkArtModel.UWB, "4", wahltagDate, "2", "wahlID2"));

            return new BasisdatenModel(Collections.emptySet(), wahlenModels, wahlbezirkeModels, Collections.emptySet());
        }
    }

}
