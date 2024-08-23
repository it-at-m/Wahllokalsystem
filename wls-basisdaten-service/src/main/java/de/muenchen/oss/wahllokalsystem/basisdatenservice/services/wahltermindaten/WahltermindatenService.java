package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltermindaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahlbezirkRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahltagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahlvorschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.KopfdatenRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.ReferendumvorlagenRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.WahlRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.BasisstrukturdatenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.InitializeKopfdaten;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.KopfdatenModelMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.WahldatenClient;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke.WahlbezirkModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke.WahlbezirkModelMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke.WahlbezirkeClient;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModelMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag.WahltagModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag.WahltagModelMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag.WahltageService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WahltermindatenService {

    private final WahltermindatenValidator wahltermindatenValidator;
    private final WahltageService wahltageService;
    private final WahldatenClient wahldatenClient;
    private final WahlbezirkeClient wahlbezirkeClient;

    private final WahltagModelMapper wahltagModelMapper;
    private final WahlModelMapper wahlModelMapper;
    private final WahlbezirkModelMapper wahlbezirkModelMapper;
    private final KopfdatenModelMapper kopfdatenModelMapper;

    private final WahlRepository wahlRepository;
    private final WahlbezirkRepository wahlbezirkRepository;
    private final KopfdatenRepository kopfdatenRepository;
    private final WahltagRepository wahltagRepository;
    private final WahlvorschlaegeRepository wahlvorschlaegeRepository;
    private final ReferendumvorlagenRepository referendumvorlagenRepository;

    private final ExceptionFactory exceptionFactory;

    @PreAuthorize("hasAuthority('Basisdaten_BUSINESSACTION_PutWahltermindaten')")
    @Transactional
    public void putWahltermindaten(final String wahltagID) {
        log.info("#putWahltermindaten");
        wahltermindatenValidator.validWahltagIDParamOrThrow(wahltagID, HttpMethod.PUT);
        val wahltagModel = wahltageService.getWahltage().stream()
                    .filter(w -> w.wahltagID().equals(wahltagID))
                    .findAny().orElse(null);
        wahltermindatenValidator.validateWahltagForSearchingWahltagID(wahltagModelMapper.toEntity(wahltagModel), HttpMethod.PUT);
        assert wahltagModel != null;
        val basisdatenModel = wahldatenClient.loadBasisdaten(wahltagModel.wahltag(), wahltagModel.nummer());
        if (null == basisdatenModel) { throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.GET_BASISDATEN_NO_DATA);}
        val wahlModels = initWahlen(wahltagModel);
        val wahlbezirkModels = initWahlbezirke(wahltagModel, wahlModels);
        final InitializeKopfdaten kopfDataInitializer = new InitializeKopfdaten(exceptionFactory, kopfdatenRepository, kopfdatenModelMapper);
        kopfDataInitializer.initKopfdaten(basisdatenModel);

        // ToDo in next Issue
        // Async
//        asyncRequests.getAsyncProgress().reset(wahltag.getWahltag(), wahltag.getNummer());
//        asyncRequests.initWahlvorschlaege(wahlen, wahlbezirke);
//        asyncRequests.initReferendumvorlagen(wahlen, wahlbezirke);

    }

    @PreAuthorize("hasAuthority('Basisdaten_BUSINESSACTION_DeleteWahltermindaten')")
    @Transactional
    public void deleteWahltermindaten(final String wahltagID) {
        log.info("#deleteWahltermindaten");
        wahltermindatenValidator.validWahltagIDParamOrThrow(wahltagID, HttpMethod.DELETE);
        val wahltag = wahltagRepository.findById(wahltagID).orElse(null);
        wahltermindatenValidator.validateWahltagForSearchingWahltagID(wahltag, HttpMethod.DELETE);
        assert wahltag != null;
        val basisstrukturdaten = wahldatenClient.loadBasisdaten(wahltag.getWahltag(), wahltag.getNummer()).basisstrukturdaten();
        val wahlIDs = basisstrukturdaten.stream().map(BasisstrukturdatenModel::wahlID).toList();

        wahlbezirkRepository.deleteByWahltag(wahltag.getWahltag());
        wahlIDs.forEach((wahlID) -> {
            kopfdatenRepository.deleteAllByBezirkUndWahlID_WahlID(wahlID);
            wahlRepository.deleteById(wahlID);
            //ToDo: Test if "orphanRemoval= true" aus dem Repository funktioniert, also ob auch jeder entsprechende Wahlvorschlag gelöscht wird
            wahlvorschlaegeRepository.deleteAllByBezirkUndWahlID_WahlID(wahlID);
            //ToDo: Test if "orphanRemoval= true" aus dem Repository funktioniert
            referendumvorlagenRepository.deleteAllByBezirkUndWahlID_WahlID(wahlID);
        });
    }

    private List<WahlModel> initWahlen(WahltagModel wahltag) {
        val wahlen = wahlModelMapper.fromListOfWahlModeltoListOfWahlEntities(
            wahldatenClient.loadBasisdaten(wahltag.wahltag(), wahltag.nummer()).wahlen().stream().toList()
        );
        wahlRepository.saveAll(wahlen);
        return wahlModelMapper.fromListOfWahlEntityToListOfWahlModel(wahlen);
    }

    private List<WahlbezirkModel> initWahlbezirke(WahltagModel wahltagModel, List<WahlModel> wahlModels) {
        val wahlbezirke = wahlbezirkModelMapper.fromListOfWahlbezirkModeltoListOfWahlbezirkEntities(
            wahlbezirkeClient.loadWahlbezirke(wahltagModel.wahltag(), wahltagModel.nummer()).stream().toList()
        );
        wahlbezirkRepository.saveAll(wahlbezirke);
        return wahlbezirkModelMapper.fromListOfWahlbezirkEntityToListOfWahlbezirkModel(wahlbezirke);
    }
}
