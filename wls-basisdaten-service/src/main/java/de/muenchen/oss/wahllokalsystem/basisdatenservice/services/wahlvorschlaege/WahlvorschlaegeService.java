package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.KandidatRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahlvorschlaege;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahlvorschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahlvorschlagRepository;
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
    public WahlvorschlaegeModel getWahlvorschlaege(@P("wahlID") final String wahlID, @P("wahlbezirkID") final String wahlbezirkID) {
        log.debug("#getWahlvorschlaege wahlID {} wahlbezirkID {}", wahlID, wahlbezirkID);

        wahlvorschlaegeValidator.validWahlIdUndWahlbezirkIDOrThrow(wahlID, wahlbezirkID);
        Wahlvorschlaege wahlvorschlaege;
        BezirkUndWahlID bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);
        val wahlvorschlaegeFromRepo = wahlvorschlaegeRepository.findByBezirkUndWahlID(bezirkUndWahlID);
        if (wahlvorschlaegeFromRepo.isEmpty()) {
            log.debug("#getWahlvorschlaege: Für Wahlbezirk {} mit WahlID {} waren keine Wahlvorschlaege in der Datenbank", wahlbezirkID, wahlID);

            val clientWahlvorschlaegeDTO = wahlvorschlaegeClient.getWahlvorschlaege(wahlID, wahlbezirkID);

            try {
                if (clientWahlvorschlaegeDTO == null) throw new IOException();
                wahlvorschlaege = wahlvorschlaegeModelMapper
                        .fromClientWahlvorschlaegeDTOtoEntity(clientWahlvorschlaegeDTO);
                val savedEntity = persistWahlvorschlagEntity(wahlvorschlaege);
                return wahlvorschlaegeModelMapper.fromEntityToWahlvorschlaegeModel(savedEntity);
            } catch (JsonProcessingException e) {
                throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.PARSE_ERROR);
            } catch (IOException e) {
                throw exceptionFactory.createInfrastrukturelleWlsException(ExceptionConstants.PARSE_ERROR);
            } catch (RuntimeException e) {
                log.error("#getWahlvorschlaege: Fehler beim Cachen", e);
                throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.UNSAVEABLE);
            }
        } else {
            return wahlvorschlaegeModelMapper.fromEntityToWahlvorschlaegeModel(wahlvorschlaegeFromRepo.get());
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
