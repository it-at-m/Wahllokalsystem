package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlbezirk.Wahlbezirk;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common.WahlbezirkArt;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlbezirk.WahlbezirkRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlenService;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag.WahltagModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag.WahltageService;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.MockDataFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
class WahlbezirkeServiceTest {
    @Mock
    WahlbezirkeValidator wahlbezirkeValidator;
    @Mock
    WahltageService wahltageService;
    @Mock
    WahlenService wahlenService;
    @Mock
    WahlbezirkRepository wahlbezirkRepository;
    @Mock
    WahlbezirkModelMapper wahlbezirkModelMapper;
    @Mock
    WahlbezirkeClient wahlbezirkeClient;

    @InjectMocks
    WahlbezirkeService unitUnderTest;

    @Nested
    class GetWahlbezirke {

        @Test
        void dataIsLoadedFromRemoteIfNotExistingInRepo() {
            val wahltagID = "_identifikatorWahltag3";
            val wahltagDate = LocalDate.now();

            val wahltag = new WahltagModel(wahltagID, wahltagDate, "", "");
            val mockedwahlbezirkeModelFromClient = MockDataFactory.createSetOfWahlbezirkModel("", wahltag.wahltag());
            val wahlen = MockDataFactory.createWahlModelList("", wahltagDate);
            val wahlModels = MockDataFactory.createWahlModelList("", wahltag.wahltag()).stream()
                    .filter((wahl) -> wahl.wahltag().equals(wahltag.wahltag())).toList();
            val mergedWahlbezirkeWithWahlen = mergeWahlbezirkeWithWahlen(mockedwahlbezirkeModelFromClient, wahlModels);
            val mergedWahlbezirkEntitiesFromRepo = buildWahlbezirEntitiesFromModels(mergedWahlbezirkeWithWahlen);

            Mockito.when(wahltageService.getWahltagByID(wahltagID)).thenReturn(wahltag);
            Mockito.when(wahlbezirkRepository.existsByWahltag(wahltag.wahltag())).thenReturn(false);
            Mockito.when(wahlbezirkeClient.loadWahlbezirke(wahltag.wahltag(), wahltag.nummer()))
                    .thenReturn(mockedwahlbezirkeModelFromClient);
            Mockito.when(wahlenService.getExistingWahlenOrderedByReihenfolge(wahltagID)).thenReturn(wahlen);
            Mockito.when(wahlbezirkRepository.findByWahltag(wahltag.wahltag())).thenReturn(mergedWahlbezirkEntitiesFromRepo);
            Mockito.when(wahlbezirkModelMapper.fromListOfWahlbezirkEntityToListOfWahlbezirkModel(mergedWahlbezirkEntitiesFromRepo))
                    .thenReturn(mergedWahlbezirkeWithWahlen);

            val result = unitUnderTest.getWahlbezirke(wahltagID);
            Assertions.assertThat(result).isEqualTo(mergedWahlbezirkeWithWahlen);

            Mockito.verify(wahlbezirkeValidator).validWahltagIDParamOrThrow(wahltagID);
        }

        @Test
        void dataIsLoadedFromRepoIfPresentAndNotFromRemoteClient() {
            val wahltagID = "_identifikatorWahltag3";

            val wahltag = new WahltagModel(wahltagID, LocalDate.now(), "", "");
            val mockedwahlbezirkeModelFromClient = MockDataFactory.createSetOfWahlbezirkModel("", wahltag.wahltag());
            val notExpectedMockedwahlbezirkeModelFromClient = MockDataFactory.createSetOfWahlbezirkModel("NotExpected", wahltag.wahltag());
            val wahlModels = MockDataFactory.createWahlModelList("", wahltag.wahltag()).stream()
                    .filter((wahl) -> wahl.wahltag().equals(wahltag.wahltag())).toList();
            val notExpectedWahlModels = MockDataFactory.createWahlModelList("NotExpected", wahltag.wahltag()).stream()
                    .filter((wahl) -> wahl.wahltag().equals(wahltag.wahltag())).toList();
            val mergedWahlbezirkeWithWahlen = mergeWahlbezirkeWithWahlen(mockedwahlbezirkeModelFromClient, wahlModels);
            val mergedWahlbezirkEntitiesFromRepo = buildWahlbezirEntitiesFromModels(mergedWahlbezirkeWithWahlen);

            val notExpectedMergedWahlbezirkeWithWahlen = mergeWahlbezirkeWithWahlen(notExpectedMockedwahlbezirkeModelFromClient, notExpectedWahlModels);

            Mockito.when(wahltageService.getWahltagByID(wahltagID)).thenReturn(wahltag);
            Mockito.when(wahlbezirkRepository.existsByWahltag(wahltag.wahltag())).thenReturn(true);

            Mockito.when(wahlbezirkRepository.findByWahltag(wahltag.wahltag())).thenReturn(mergedWahlbezirkEntitiesFromRepo);
            Mockito.when(wahlbezirkModelMapper.fromListOfWahlbezirkEntityToListOfWahlbezirkModel(mergedWahlbezirkEntitiesFromRepo))
                    .thenReturn(mergedWahlbezirkeWithWahlen);

            val result = unitUnderTest.getWahlbezirke(wahltagID);
            Assertions.assertThat(result).isEqualTo(mergedWahlbezirkeWithWahlen);
            Assertions.assertThat(result).isNotEqualTo(notExpectedMergedWahlbezirkeWithWahlen);

            Mockito.verify(wahlbezirkeValidator).validWahltagIDParamOrThrow(wahltagID);
        }
    }

    private List<Wahlbezirk> buildWahlbezirEntitiesFromModels(List<WahlbezirkModel> remoteSetWahlbezirkeModels) {
        List<Wahlbezirk> remoteList = new ArrayList<>();
        remoteSetWahlbezirkeModels.forEach((wbModel) -> remoteList
                .add(new Wahlbezirk(wbModel.wahlbezirkID(), wbModel.wahltag(), wbModel.nummer(), WahlbezirkArt.valueOf(wbModel.wahlbezirkart().name()),
                        wbModel.wahlnummer(), wbModel.wahlID())));
        return remoteList;
    }

    private List<WahlbezirkModel> mergeWahlbezirkeWithWahlen(Set<WahlbezirkModel> remoteSetWahlbezirkeModels, List<WahlModel> wahlModels) {
        List<Wahlbezirk> responselist = new ArrayList<>();
        List<Wahlbezirk> remoteList = buildWahlbezirEntitiesFromModels(remoteSetWahlbezirkeModels.stream().toList());
        remoteList.forEach(wahlbezirk -> {
            if (null != wahlModels) {
                wahlModels.stream().filter(wahl -> wahlbezirk.getWahlnummer().equals(wahl.nummer())).findFirst()
                        .ifPresent(searchedWahl -> wahlbezirk.setWahlID(searchedWahl.wahlID()));
            }
            responselist.add(wahlbezirk);
        });
        List<WahlbezirkModel> responseModelList = new ArrayList<>();
        responselist.forEach((wahlbezirk) -> responseModelList.add(new WahlbezirkModel(
                wahlbezirk.getWahlbezirkID(),
                WahlbezirkArtModel.valueOf(wahlbezirk.getWahlbezirkart().name()),
                wahlbezirk.getNummer(),
                wahlbezirk.getWahltag(),
                wahlbezirk.getWahlnummer(),
                wahlbezirk.getWahlID())));
        return responseModelList;
    }
}
