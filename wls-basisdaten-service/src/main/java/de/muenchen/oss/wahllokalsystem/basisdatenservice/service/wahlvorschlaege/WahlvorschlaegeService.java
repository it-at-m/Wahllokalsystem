package de.muenchen.oss.wahllokalsystem.basisdatenservice.service.wahlvorschlaege;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.aoueai.template.AoueaiServiceTemplate;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WLSWahlvorschlaege;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WLSWahlvorschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WahlvorschlaegeService {

    private final WLSWahlvorschlaegeRepository wahlvorschlaegeRepository;
    private final WahlvorschlaegeModelMapper wahlvorschlaegeModelMapper;
    private final WahlvorschlaegeValidator wahlvorschlaegeValidator;

    @Autowired
    AoueaiServiceTemplate aoueaiServiceTemplate;


    @PreAuthorize(
        "hasAuthority('Basisdaten_BUSINESSACTION_GetWahlvorschlaege')"
    )
    public String getWahlvorschlaege(@P("wahlID") final String wahlID, @P("wahlbezirkID") final String wahlbezirkID) {
        log.debug("#getWahlvorschlaege wahlID {} wahlbezirkID {}", wahlID, wahlbezirkID);

        wahlvorschlaegeValidator.validWahlIdUndWahlbezirkIDOrThrow(wahlID, wahlbezirkID);

        if (wahlvorschlaegeRepository.countByBezirkUndWahlID(new BezirkUndWahlID(wahlID, wahlbezirkID)) == 0) {
            log.debug("#getWahlvorschlaege: Für Wahlbezirk {} mit WahlID {} waren keine Wahlvorschlaege in der Datenbank", wahlbezirkID, wahlID);
            WLSWahlvorschlaege wlsWahlvorschlaege = wahlvorschlaegeModelMapper.toEntity(aoueaiServiceTemplate.getWahlvorschlaege(wahlID, wahlbezirkID));

            try {
                wahlvorschlaegeRepository.save(wlsWahlvorschlaege);
            } catch (Exception e) {
                log.error("#getWahlvorschlaege: Fehler beim Cachen", e);
            }
            return wlsWahlvorschlaege.getWahlvorschlaegeAsJson();
        }

        val dataFromRepo = wahlvorschlaegeRepository.findById( new BezirkUndWahlID(wahlID, wahlbezirkID));

        if (dataFromRepo == null) {
            log.debug("#getWahlvorschlaege: Für Wahlbezirk {} mit WahlID {} waren keine Wahlvorschlaege in der Datenbank", wahlbezirkID, wahlID);
        }

        val mappedData = dataFromRepo.orElse(null);
        return mappedData.getWahlvorschlaegeAsJson();
    }

}
