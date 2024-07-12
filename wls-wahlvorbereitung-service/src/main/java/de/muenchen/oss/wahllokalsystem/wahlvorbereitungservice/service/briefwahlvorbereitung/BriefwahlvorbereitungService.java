package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.briefwahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.BriefwahlvorbereitungRepository;
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
public class BriefwahlvorbereitungService {

    private final BriefwahlvorbereitungRepository briefwahlvorbereitungRepository;
    private final BriefwahlvorbereitungModelMapper briefwahlvorbereitungModelMapper;
    private final BriefwahlvorbereitungValidator briefwahlvorbereitungValidator;
    private final ExceptionFactory exceptionFactory;

    @PreAuthorize(
        "hasAuthority('Wahlvorbereitung_BUSINESSACTION_GetBriefwahlvorbereitung')"
                + "and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#wahlbezirkID, authentication)"
    )
    public Optional<BriefwahlvorbereitungModel> getBriefwahlvorbereitung(@P("wahlbezirkID") final String wahlbezirkID) {
        log.debug("#getBriefwahlvorbereitung");
        log.debug("in: wahlbezirkID > {}", wahlbezirkID);

        briefwahlvorbereitungValidator.validWahlbezirkIDOrThrow(wahlbezirkID);

        val dataFromRepo = briefwahlvorbereitungRepository.findById(wahlbezirkID);

        log.debug("out: briefwahlvorbereitung > {}", dataFromRepo.orElse(null));

        return dataFromRepo.map(briefwahlvorbereitungModelMapper::toModel);
    }

    @PreAuthorize(
        "hasAuthority('Wahlvorbereitung_BUSINESSACTION_PostBriefwahlvorbereitung')"
                + "and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#briefwahlvorbereitungToSet.wahlbezirkID(), authentication)"
    )
    public void setBriefwahlvorbereitung(@P("briefwahlvorbereitungToSet") final BriefwahlvorbereitungModel briefwahlvorbereitungToSet) {
        log.debug("#postBriefwahlvorbereitung");
        log.debug("in: briefwahlvorbereitung > {}", briefwahlvorbereitungToSet);

        briefwahlvorbereitungValidator.validModelToSetOrThrow(briefwahlvorbereitungToSet);

        try {
            briefwahlvorbereitungRepository.save(briefwahlvorbereitungModelMapper.toEntity(briefwahlvorbereitungToSet));
        } catch (final Exception e) {
            log.error("Fehler beim speichern: ", e);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.UNSAVEABLE);
        }
    }
}
