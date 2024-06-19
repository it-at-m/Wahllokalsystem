package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.aoueai.template.AoueaiServiceTemplate;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WLSWahlvorschlaege;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WLSWahlvorschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WahlvorschlaegeService {

    private final WLSWahlvorschlaegeRepository wahlvorschlaegeRepository;
    private final WahlvorschlaegeClientWLSMapper wahlvorschlaegeClientWLSMapper;
    private final WahlvorschlaegeValidator wahlvorschlaegeValidator;
    private final WahlvorschlaegeClient wahlvorschlaegeClient;

    private final ExceptionFactory exceptionFactory;

    @PreAuthorize(
        "hasAuthority('Basisdaten_BUSINESSACTION_GetWahlvorschlaege')"
    )
    public String getWahlvorschlaege(@P("wahlID") final String wahlID, @P("wahlbezirkID") final String wahlbezirkID) {
        log.debug("#getWahlvorschlaege wahlID {} wahlbezirkID {}", wahlID, wahlbezirkID);

        wahlvorschlaegeValidator.validWahlIdUndWahlbezirkIDOrThrow(wahlID, wahlbezirkID);
        WLSWahlvorschlaege wlsWahlvorschlaege;
        BezirkUndWahlID bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);
        if (wahlvorschlaegeRepository.countByBezirkUndWahlID(bezirkUndWahlID) == 0) {
            log.debug("#getWahlvorschlaege: Für Wahlbezirk {} mit WahlID {} waren keine Wahlvorschlaege in der Datenbank", wahlbezirkID, wahlID);

            val clientWahlvorschlaegeDTO = wahlvorschlaegeClient.getWahlvorschlaege(wahlID, wahlbezirkID);

            try {
                if (clientWahlvorschlaegeDTO == null) throw new IOException();
                wlsWahlvorschlaege = wahlvorschlaegeClientWLSMapper.fromClientDTOtoWLSEntity(wahlvorschlaegeClient.getWahlvorschlaege(wahlID, wahlbezirkID));
                wahlvorschlaegeRepository.save(wlsWahlvorschlaege);
            } catch (JsonProcessingException e) {
                throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.PARSE_ERROR);
            } catch (IOException e) {
                throw exceptionFactory.createInfrastrukturelleWlsException(ExceptionConstants.PARSE_ERROR);
            } catch (RuntimeException e) {
                log.error("#getWahlvorschlaege: Fehler beim Cachen", e);
                throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.UNSAVEABLE);
            }
        }
        return wahlvorschlaegeRepository.findById(bezirkUndWahlID).get().getWahlvorschlaegeAsJson();
    }

}
