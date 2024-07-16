package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.eroeffnungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.EroeffnungsUhrzeitRepository;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
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
public class EroeffnungsUhrzeitService {

    private final EroeffnungsUhrzeitRepository eroeffnungsUhrzeitRepository;
    private final EroeffnungsUhrzeitModelMapper eroeffnungsUhrzeitModelMapper;
    private final EroeffnungsUhrzeitValidator eroeffnungsUhrzeitValidator;
    private final ExceptionFactory exceptionFactory;

    @PreAuthorize(
        "hasAuthority('Wahlvorbereitung_BUSINESSACTION_GetEroeffnungsuhrzeit')"
                + "and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#wahlbezirkID, authentication)"
    )
    public Optional<EroeffnungsUhrzeitModel> getEroeffnungsUhrzeit(@P("wahlbezirkID") final String wahlbezirkID) {
        log.debug("#getEroeffnungsUhrzeit");
        log.debug("in: wahlbezirkID > {}", wahlbezirkID);

        eroeffnungsUhrzeitValidator.validWahlbezirkIDOrThrow(wahlbezirkID);

        val dataFromRepo = eroeffnungsUhrzeitRepository.findById(wahlbezirkID);

        log.debug("out: eroeffnungsuhrzeit > {}", dataFromRepo.orElse(null));

        return dataFromRepo.map(eroeffnungsUhrzeitModelMapper::toModel);
    }

    @PreAuthorize(
        "hasAuthority('Wahlvorbereitung_BUSINESSACTION_PostEroeffnungsuhrzeit')"
                + "and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#eroeffnungsuhrzeitToSet.wahlbezirkID(), authentication)"
    )
    public void setEroeffnungsUhrzeit(@P("eroeffnungsuhrzeitToSet") final EroeffnungsUhrzeitModel eroeffnungsuhrzeitToSet) {
        log.debug("#postEroeffnungsuhrzeit");
        log.debug("in: eroeffnungsUhrzeit > {}", eroeffnungsuhrzeitToSet);

        eroeffnungsUhrzeitValidator.validModelToSetOrThrow(eroeffnungsuhrzeitToSet);

        try {
            val eroeffnungsuhrzeitToSave = eroeffnungsUhrzeitModelMapper.toEntity(eroeffnungsuhrzeitToSet);
            eroeffnungsUhrzeitRepository.save(eroeffnungsuhrzeitToSave);
            // Fachliches Logging mit  SIEM
            try {
                MDC.put("eid", "EROEFFNUNG");
                MDC.put("result", "0");
                log.info("openingTime={}|", eroeffnungsuhrzeitToSave.getEroeffnungsuhrzeit());
            } finally {
                MDC.remove("eid");
                MDC.remove("result");
            }
        } catch (Exception e) {
            log.error("Fehler beim speichern: ", e);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.UNSAVEABLE);
        }
    }
}
