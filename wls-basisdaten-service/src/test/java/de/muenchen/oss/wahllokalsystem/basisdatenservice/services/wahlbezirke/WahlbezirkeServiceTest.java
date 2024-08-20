package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahlbezirk;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahlbezirkArt;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahlbezirkRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahltagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.WahlRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModelMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.MockDataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    WahltagRepository wahltagRepository;
    @Mock
    WahlRepository wahlRepository;
    @Mock
    ExceptionFactory exceptionFactory;
    @Mock
    WahlbezirkRepository wahlbezirkRepository;
    @Mock
    WahlbezirkModelMapper wahlbezirkModelMapper;
    @Mock
    WahlModelMapper wahlModelMapper;
    @Mock
    WahlbezirkeClient wahlbezirkeClient;

    @InjectMocks
    WahlbezirkeService unitUnderTest;

    @Nested
    class GetWahlbezirke {

        @Test
        void throwsFachlicheWlsExceptionIfNoWahltagPresentInRepository() {
            val wahltagID = "_identifikatorWahltag1";
            Mockito.when(wahltagRepository.findById(wahltagID)).thenReturn(Optional.empty());
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.CODE_GETWAHLBEZIRKE_NO_WAHLTAG))
                    .thenReturn(mockedWlsException);
            Assertions.assertThatThrownBy(() -> unitUnderTest.getWahlbezirke(wahltagID)).isInstanceOf(FachlicheWlsException.class);
            Mockito.verify(wahlbezirkeValidator).validWahltagIDParamOrThrow(wahltagID);
        }

        @Test
        void dataIsLoadedFromRemoteIfNotExistingInRepo() {
            val wahltagID = "_identifikatorWahltag3";

            val wahltag = MockDataFactory.createWahltagList("").stream().filter((wtg) -> wtg.getWahltagID().equals(wahltagID)).findFirst();
            val mockedwahlbezirkeModelFromClient = MockDataFactory.createSetOfWahlbezirkModel("", wahltag.get().getWahltag());
            val wahlen = MockDataFactory.createWahlEntityList().stream().filter((wahl) -> wahl.getWahltag().equals(wahltag.get().getWahltag())).toList();
            val wahlModels = MockDataFactory.createWahlModelList("", wahltag.get().getWahltag()).stream()
                    .filter((wahl) -> wahl.wahltag().equals(wahltag.get().getWahltag())).toList();
            val mergedWahlbezirkeWithWahlen = mergeWahlbezirkeWithWahlen(mockedwahlbezirkeModelFromClient, wahlModels);
            val mergedWahlbezirkEntitiesFromRepo = buildWahlbezirEntitiesFromModels(mergedWahlbezirkeWithWahlen);

            Mockito.when(wahltagRepository.findById(wahltagID)).thenReturn(wahltag);
            Mockito.when(wahlbezirkRepository.countByWahltag(wahltag.get().getWahltag())).thenReturn(0);
            Mockito.when(wahlbezirkeClient.loadWahlbezirke(wahltag.get().getWahltag(), wahltag.get().getNummer()))
                    .thenReturn(mockedwahlbezirkeModelFromClient);
            Mockito.when(wahlRepository.findByWahltagOrderByReihenfolge(wahltag.get().getWahltag())).thenReturn(wahlen);
            Mockito.when(wahlModelMapper.fromListOfWahlEntityToListOfWahlModel(wahlen)).thenReturn(wahlModels);
            Mockito.when(wahlbezirkModelMapper.toWahlbezirkModelListMergedWithWahlenInfo(mockedwahlbezirkeModelFromClient, wahlModels, exceptionFactory))
                    .thenReturn(mergedWahlbezirkeWithWahlen);
            Mockito.when(wahlbezirkRepository.findByWahltag(wahltag.get().getWahltag())).thenReturn(mergedWahlbezirkEntitiesFromRepo);
            Mockito.when(wahlbezirkModelMapper.fromListOfWahlbezirkEntityToListOfWahlbezirkModel(mergedWahlbezirkEntitiesFromRepo))
                    .thenReturn(mergedWahlbezirkeWithWahlen);

            val result = unitUnderTest.getWahlbezirke(wahltagID);
            Assertions.assertThat(result).isEqualTo(mergedWahlbezirkeWithWahlen);

            Mockito.verify(wahlbezirkeValidator).validWahltagIDParamOrThrow(wahltagID);
            Mockito.verify(wahlbezirkeValidator).validateWahltagForSearchingWahltagID(wahltag);
        }

        @Test
        void dataIsLoadedFromRepoIfPresentAndNotFromRemoteClient() {
            val wahltagID = "_identifikatorWahltag3";

            val wahltag = MockDataFactory.createWahltagList("").stream().filter((wtg) -> wtg.getWahltagID().equals(wahltagID)).findFirst();
            val mockedwahlbezirkeModelFromClient = MockDataFactory.createSetOfWahlbezirkModel("", wahltag.get().getWahltag());
            val notExpectedMockedwahlbezirkeModelFromClient = MockDataFactory.createSetOfWahlbezirkModel("NotExpected", wahltag.get().getWahltag());
            val wahlen = MockDataFactory.createWahlEntityList().stream().filter((wahl) -> wahl.getWahltag().equals(wahltag.get().getWahltag())).toList();
            val wahlModels = MockDataFactory.createWahlModelList("", wahltag.get().getWahltag()).stream()
                    .filter((wahl) -> wahl.wahltag().equals(wahltag.get().getWahltag())).toList();
            val notExpectedWahlModels = MockDataFactory.createWahlModelList("NotExpected", wahltag.get().getWahltag()).stream()
                    .filter((wahl) -> wahl.wahltag().equals(wahltag.get().getWahltag())).toList();
            val mergedWahlbezirkeWithWahlen = mergeWahlbezirkeWithWahlen(mockedwahlbezirkeModelFromClient, wahlModels);
            val mergedWahlbezirkEntitiesFromRepo = buildWahlbezirEntitiesFromModels(mergedWahlbezirkeWithWahlen);

            val notExpectedMergedWahlbezirkeWithWahlen = mergeWahlbezirkeWithWahlen(notExpectedMockedwahlbezirkeModelFromClient, notExpectedWahlModels);

            Mockito.when(wahltagRepository.findById(wahltagID)).thenReturn(wahltag);
            Mockito.when(wahlbezirkRepository.countByWahltag(wahltag.get().getWahltag())).thenReturn(3);

            Mockito.lenient().when(wahlbezirkeClient.loadWahlbezirke(wahltag.get().getWahltag(), wahltag.get().getNummer()))
                    .thenReturn(notExpectedMockedwahlbezirkeModelFromClient);
            Mockito.lenient().when(wahlRepository.findByWahltagOrderByReihenfolge(wahltag.get().getWahltag())).thenReturn(wahlen);
            Mockito.lenient().when(wahlModelMapper.fromListOfWahlEntityToListOfWahlModel(wahlen)).thenReturn(notExpectedWahlModels);
            Mockito.lenient()
                    .when(wahlbezirkModelMapper.toWahlbezirkModelListMergedWithWahlenInfo(notExpectedMockedwahlbezirkeModelFromClient, notExpectedWahlModels,
                            exceptionFactory))
                    .thenReturn(mergedWahlbezirkeWithWahlen);

            Mockito.when(wahlbezirkRepository.findByWahltag(wahltag.get().getWahltag())).thenReturn(mergedWahlbezirkEntitiesFromRepo);
            Mockito.when(wahlbezirkModelMapper.fromListOfWahlbezirkEntityToListOfWahlbezirkModel(mergedWahlbezirkEntitiesFromRepo))
                    .thenReturn(mergedWahlbezirkeWithWahlen);

            val result = unitUnderTest.getWahlbezirke(wahltagID);
            Assertions.assertThat(result).isEqualTo(mergedWahlbezirkeWithWahlen);
            Assertions.assertThat(result).isNotEqualTo(notExpectedMergedWahlbezirkeWithWahlen);

            Mockito.verify(wahlbezirkeValidator).validWahltagIDParamOrThrow(wahltagID);
            Mockito.verify(wahlbezirkeValidator).validateWahltagForSearchingWahltagID(wahltag);
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
