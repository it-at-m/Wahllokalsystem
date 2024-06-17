package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlschliessungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UrnenwahlSchliessungsUhrzeitRepository;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.slf4j.MDC;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrnenwahlSchliessungsUhrzeitService {

    private final UrnenwahlSchliessungsUhrzeitRepository urnenwahlSchliessungsUhrzeitRepository;
    private final UrnenwahlSchliessungsUhrzeitModelMapper urnenwahlSchliessungsUhrzeitModelMapper;
    private final UrnenwahlSchliessungsUhrzeitValidator urnenwahlSchliessungsUhrzeitValidator;
    private final ExceptionFactory exceptionFactory;

    @PreAuthorize(
        "hasAuthority('Wahlvorbereitung_BUSINESSACTION_GetUrnenwahlSchliessungsuhrzeit')"
            + "and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#wahlbezirkID, authentication)"
    )
    public Optional<UrnenwahlSchliessungsUhrzeitModel> getUrnenwahlSchliessungsUhrzeit(@P("wahlbezirkID") final String wahlbezirkID) {
        log.debug("#getUrnenwahlSchliessungsUhrzeit");
        log.debug("in: wahlbezirkID > {}", wahlbezirkID);

        urnenwahlSchliessungsUhrzeitValidator.validWahlbezirkIDOrThrow(wahlbezirkID);

        val dataFromRepo = urnenwahlSchliessungsUhrzeitRepository.findById(wahlbezirkID);

        log.debug("out: urnenwahlSchliessungsUhrzeit > {}", dataFromRepo.orElse(null));

        return dataFromRepo.map(urnenwahlSchliessungsUhrzeitModelMapper::toModel);
    }

    @PreAuthorize(
        "hasAuthority('Wahlvorbereitung_BUSINESSACTION_PostUrnenwahlSchliessungsuhrzeit')"
            + "and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#urnenwahlSchliessungsUhrzeitToSet.wahlbezirkID(), authentication)"
    )
    public void setUrnenwahlSchliessungsUhrzeit(
        @P("urnenwahlSchliessungsUhrzeitToSet") final UrnenwahlSchliessungsUhrzeitModel urnenwahlSchliessungsUhrzeitToSet) {
        log.debug("#postUrnenwahlSchliessungsUhrzeit");
        log.debug("in: urnenwahlSchliessungsUhrzeit > {}", urnenwahlSchliessungsUhrzeitToSet);

        urnenwahlSchliessungsUhrzeitValidator.validModelToSetOrThrow(urnenwahlSchliessungsUhrzeitToSet);

        try {
            val urnenwahlSchliessungsUhrzeitToSave = urnenwahlSchliessungsUhrzeitModelMapper.toEntity(urnenwahlSchliessungsUhrzeitToSet);
            urnenwahlSchliessungsUhrzeitRepository.save(urnenwahlSchliessungsUhrzeitToSave);
            // Fachliches Logging mit  SIEM
            try {
                MDC.put("eid", "EROEFFNUNG");
                MDC.put("result", "0");
                log.info("openingTime={}|", urnenwahlSchliessungsUhrzeitToSave.getUrnenwahlSchliessungsUhrzeit());
            } finally {
                MDC.remove("eid");
                MDC.remove("result");
            }
        } catch (final Exception e) {
            log.error("Fehler beim speichern: ", e);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.UNSAVEABLE);
        }
    }
}
