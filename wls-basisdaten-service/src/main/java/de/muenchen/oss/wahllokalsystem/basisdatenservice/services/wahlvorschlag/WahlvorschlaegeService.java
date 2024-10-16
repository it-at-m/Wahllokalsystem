package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlag;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.KandidatRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.WahlvorschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.WahlvorschlagRepository;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @PreAuthorize(
        "hasAuthority('Basisdaten_BUSINESSACTION_GetWahlvorschlaege')"
    )
    @Transactional
    public WahlvorschlaegeModel getWahlvorschlaege(final BezirkUndWahlID bezirkUndWahlID) {
        log.debug("#getWahlvorschlaege bezirkUndWahlID > {}", bezirkUndWahlID);

        wahlvorschlaegeValidator.validWahlIdUndWahlbezirkIDOrThrow(bezirkUndWahlID);
        val wahlvorschlaegeFromRepo = wahlvorschlaegeRepository.findByBezirkUndWahlID(bezirkUndWahlID);
        if (wahlvorschlaegeFromRepo.isEmpty()) {
            log.debug("#getWahlvorschlaege: Für BezirkUndWahlID {} waren keine Wahlvorschlaege in der Datenbank", bezirkUndWahlID);
            val importedWahlvorschlaegeModel = wahlvorschlaegeClient.getWahlvorschlaege(bezirkUndWahlID);
            try {
                val savedWahlvorschlaege = wahlvorschlaegeRepository.save(wahlvorschlaegeModelMapper.toEntity(importedWahlvorschlaegeModel));
                return wahlvorschlaegeModelMapper.toModel(savedWahlvorschlaege);
            } catch (final Exception exception) {
                log.error("#getWahlvorschlaege: Fehler beim Cachen", exception);
                return importedWahlvorschlaegeModel; // an exception on saving does prevent sending a response. We can use the imported object
            }
        } else {
            return wahlvorschlaegeModelMapper.toModel(wahlvorschlaegeFromRepo.get());
        }
    }

}
