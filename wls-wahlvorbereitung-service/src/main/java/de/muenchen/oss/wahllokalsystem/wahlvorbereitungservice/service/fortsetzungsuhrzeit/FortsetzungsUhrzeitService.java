package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.fortsetzungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.FortsetzungsUhrzeitRepository;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FortsetzungsUhrzeitService {

    private final FortsetzungsUhrzeitRepository fortsetzungsUhrzeitRepository;
    private final FortsetzungsUhrzeitModelMapper fortsetzungsUhrzeitModelMapper;
    private final FortsetzungsUhrzeitValidator fortsetzungsUhrzeitValidator;
    private final ExceptionFactory exceptionFactory;

    @PreAuthorize(
        "hasAuthority('Wahlvorbereitung_BUSINESSACTION_FortsetzungsUhrzeit')"
                + "and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#wahlbezirkID, authentication)"
    )
    public Optional<FortsetzungsUhrzeitModel> getFortsetzungsUhrzeit(@P("wahlbezirkID") final String wahlbezirkID) {
        log.debug("#getFortsetzungsUhrzeit");
        log.debug("in: wahlbezirkID > {}", wahlbezirkID);

        fortsetzungsUhrzeitValidator.validWahlbezirkIDOrThrow(wahlbezirkID);

        val dataFromRepo = fortsetzungsUhrzeitRepository.findById(wahlbezirkID);

        log.debug("out: fortsetzungsUhrzeit > {}", dataFromRepo.orElse(null));

        return dataFromRepo.map(fortsetzungsUhrzeitModelMapper::toModel);
    }

    @PreAuthorize(
        "hasAuthority('Wahlvorbereitung_BUSINESSACTION_FortsetzungsUhrzeit')"
                + "and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#fortsetzungsUhrzeitToSet.wahlbezirkID(), authentication)"
    )
    public void setFortsetzungsUhrzeit(@P("fortsetzungsUhrzeitToSet") final FortsetzungsUhrzeitModel fortsetzungsUhrzeitToSet) {
        log.debug("#postFortsetzungsUhrzeit");
        log.debug("in: fortsetzungsUhrzeit > {}", fortsetzungsUhrzeitToSet);

        fortsetzungsUhrzeitValidator.validModelToSetOrThrow(fortsetzungsUhrzeitToSet);

        try {
            fortsetzungsUhrzeitRepository.save(fortsetzungsUhrzeitModelMapper.toEntity(fortsetzungsUhrzeitToSet));
        } catch (Exception e) {
            log.error("Fehler beim speichern: ", e);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.UNSAVEABLE);
        }
    }
}
