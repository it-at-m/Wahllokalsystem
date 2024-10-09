package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltermindaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.KopfdatenRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.ReferendumvorlagenRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlbezirke.Wahlbezirk;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlbezirke.WahlbezirkRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.WahlRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.WahlvorschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.BasisstrukturdatenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.InitializeKopfdaten;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.KopfdatenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.KopfdatenModelMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.WahldatenClient;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke.WahlbezirkModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke.WahlbezirkModelMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModelMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahltagWithNummer;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag.WahltagModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag.WahltageService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
//TODO create Issue for Phase 3: this Service is a fassade for other services: wahlen, wahlbezirke, ...
public class WahltermindatenService {

    private final WahltermindatenValidator wahltermindatenValidator;
    private final WahltageService wahltageService;
    private final WahldatenClient wahldatenClient;

    private final WahlModelMapper wahlModelMapper;
    private final WahlbezirkModelMapper wahlbezirkModelMapper;
    private final KopfdatenModelMapper kopfdatenModelMapper;

    private final WahlRepository wahlRepository;
    private final WahlbezirkRepository wahlbezirkRepository;
    private final KopfdatenRepository kopfdatenRepository;
    private final WahlvorschlaegeRepository wahlvorschlaegeRepository;
    private final ReferendumvorlagenRepository referendumvorlagenRepository;
    private final InitializeKopfdaten kopfDataInitializer;

    private final AsyncWahltermindatenService asyncWahltermindatenService;

    private final ExceptionFactory exceptionFactory;

    @PreAuthorize("hasAuthority('Basisdaten_BUSINESSACTION_PutWahltermindaten')")
    @Transactional
    public void putWahltermindaten(final String wahltagID) {
        log.info("#putWahltermindaten");
        wahltermindatenValidator.validateParameterToInitWahltermindaten(wahltagID);
        val wahltagModel = getWahltagByIdOrThrow(wahltagID,
                () -> exceptionFactory.createFachlicheWlsException(ExceptionConstants.CODE_PUTWAHLTERMINDATEN_NO_WAHLTAG));

        val basisdatenModel = wahldatenClient.loadBasisdaten(new WahltagWithNummer(wahltagModel.wahltag(), wahltagModel.nummer()));
        if (null == basisdatenModel) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.GET_BASISDATEN_NO_DATA);
        }

        persistWahlen(basisdatenModel.wahlen().stream().toList());
        persistWahlbezirke(basisdatenModel.wahlbezirke(), basisdatenModel.wahlen());
        persistKopfdaten(kopfDataInitializer.initKopfdaten(basisdatenModel));

        asyncWahltermindatenService.initVorlagenAndVorschlaege(wahltagModel.wahltag(), wahltagModel.nummer(), basisdatenModel);

    }

    @PreAuthorize("hasAuthority('Basisdaten_BUSINESSACTION_DeleteWahltermindaten')")
    @Transactional
    public void deleteWahltermindaten(final String wahltagID) {
        log.info("#deleteWahltermindaten");
        wahltermindatenValidator.validateParameterToDeleteWahltermindaten(wahltagID);
        val wahltag = getWahltagByIdOrThrow(wahltagID,
                () -> exceptionFactory.createFachlicheWlsException(ExceptionConstants.CODE_DELETEWAHLTERMINDATEN_PARAMETER_UNVOLLSTAENDIG));

        val basisstrukturdaten = wahldatenClient.loadBasisdaten(new WahltagWithNummer(wahltag.wahltag(), wahltag.nummer())).basisstrukturdaten();
        val wahlIDs = basisstrukturdaten.stream().map(BasisstrukturdatenModel::wahlID).toList();

        wahlbezirkRepository.deleteByWahltag(wahltag.wahltag());
        wahlIDs.forEach((wahlID) -> {
            kopfdatenRepository.deleteAllByBezirkUndWahlID_WahlID(wahlID);
            wahlRepository.deleteById(wahlID);
            wahlvorschlaegeRepository.deleteAllByBezirkUndWahlID_WahlID(wahlID);
            referendumvorlagenRepository.deleteAllByBezirkUndWahlID_WahlID(wahlID);
        });
    }

    private WahltagModel getWahltagByIdOrThrow(final String wahltagID, final Supplier<FachlicheWlsException> wlsExceptionSupplier) {
        val wahltagModel = wahltageService.getWahltage().stream() //TODO wie wäre es mit getOptionalById oder findById aus dem WahltageService?
                .filter(w -> w.wahltagID().equals(wahltagID))
                .findAny();
        return wahltagModel.orElseThrow(wlsExceptionSupplier);
    }

    private void persistKopfdaten(List<KopfdatenModel> kopfdatenModels) {
        val kopfdatenEntities = kopfdatenModels.stream().map(kopfdatenModelMapper::toEntity).toList();
        kopfdatenRepository.saveAll(kopfdatenEntities);
    }

    private void persistWahlen(final List<WahlModel> wahlModelIterable) {
        val wahlenEntities = wahlModelMapper.fromListOfWahlModeltoListOfWahlEntities(wahlModelIterable);
        wahlRepository.saveAll(wahlenEntities);
    }

    private void persistWahlbezirke(Collection<WahlbezirkModel> wahlbezirkModelIterable, Collection<WahlModel> wahlModels) {
        val wahlbezirke = wahlbezirkModelMapper.fromListOfWahlbezirkModeltoListOfWahlbezirkEntities(wahlbezirkModelIterable);
        wahlbezirke.forEach(wahlbezirkEntity -> linkFirstMatchingWahl(wahlbezirkEntity, wahlModels));
        wahlbezirkRepository.saveAll(wahlbezirke);
    }

    private void linkFirstMatchingWahl(final Wahlbezirk wahlbezirk, final Collection<WahlModel> wahlen) {
        val searchedWahl = wahlen.stream().filter(wahl -> wahlbezirk.getWahlnummer().equals(wahl.nummer())).findFirst().orElse(null);
        if (null != searchedWahl) {
            wahlbezirk.setWahlID(searchedWahl.wahlID());
        }
    }
}
