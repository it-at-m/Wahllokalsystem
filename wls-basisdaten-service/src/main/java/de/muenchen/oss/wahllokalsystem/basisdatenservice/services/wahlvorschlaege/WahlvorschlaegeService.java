package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.KandidatRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahlvorschlaege;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahlvorschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahlvorschlagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WahlvorschlaegeService {

    private final WahlvorschlaegeRepository wahlvorschlaegeRepository;
    private final WahlvorschlagRepository wahlvorschlagRepository;
    private final KandidatRepository kandidatRepository;
    private final WahlvorschlaegeModelMapper wahlvorschlaegeModelMapper;
    private final WahlvorschlaegeValidator wahlvorschlaegeValidator;
    private final WahlvorschlaegeClient wahlvorschlaegeClient;

    private final ExceptionFactory exceptionFactory;

    @PreAuthorize(
        "hasAuthority('Basisdaten_BUSINESSACTION_GetWahlvorschlaege')"
    )
    public WahlvorschlaegeModel getWahlvorschlaege(final BezirkUndWahlID bezirkUndWahlID) {
        log.debug("#getWahlvorschlaege bezirkUndWahlID > {}", bezirkUndWahlID);

        wahlvorschlaegeValidator.validWahlIdUndWahlbezirkIDOrThrow(bezirkUndWahlID);
        val wahlvorschlaegeFromRepo = wahlvorschlaegeRepository.findByBezirkUndWahlID(bezirkUndWahlID);
        if (wahlvorschlaegeFromRepo.isEmpty()) {
            val savedWahlvorschlaege = importAndSaveWahlvorschlaege(bezirkUndWahlID);
            return wahlvorschlaegeModelMapper.toModel(savedWahlvorschlaege);
        } else {
            return wahlvorschlaegeModelMapper.toModel(wahlvorschlaegeFromRepo.get());
        }
    }

    private Wahlvorschlaege importAndSaveWahlvorschlaege(BezirkUndWahlID bezirkUndWahlID) {
        log.debug("#getWahlvorschlaege: Für BezirkUndWahlID {} waren keine Wahlvorschlaege in der Datenbank", bezirkUndWahlID);
        val clientWahlvorschlaegeDTO = wahlvorschlaegeClient.getWahlvorschlaege(bezirkUndWahlID);
        try {
            val entityToCreate = wahlvorschlaegeModelMapper.toEntity(clientWahlvorschlaegeDTO);
            return persistWahlvorschlagEntity(entityToCreate);
        } catch (RuntimeException e) {
            log.error("#getWahlvorschlaege: Fehler beim Cachen", e);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.UNSAVEABLE);
        }
    }

    protected Wahlvorschlaege persistWahlvorschlagEntity(final Wahlvorschlaege wahlvorschlaege) {
        wahlvorschlaegeRepository.save(wahlvorschlaege);
        wahlvorschlaege.getWahlvorschlaege().forEach(wahlvorschlag -> {
            wahlvorschlagRepository.save(wahlvorschlag);
            kandidatRepository.saveAll(wahlvorschlag.getKandidaten());
        });

        return wahlvorschlaege;
    }

}
