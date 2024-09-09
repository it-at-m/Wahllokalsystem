package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.EreignisRepository;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Provides a service to execute business-actions.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EreignisService {

    private final EreignisRepository ereignisRepository;
    private final ExceptionFactory exceptionFactory;
    private final EreignisModelMapper ereignisModelMapper;
    private final EreignisValidator ereignisValidator;

    static final String GET_EREIGNIS = "hasAuthority('VorfaelleUndVorkommnisse_BUSINESSACTION_GetEreignisse')";
    static final String POST_EREIGNIS = "hasAuthority('VorfaelleUndVorkommnisse_BUSINESSACTION_PostEreignisse')";

    /**
     * This BusinessAction's purpose is: Laden der Ereignisse It returns one Ereignis.
     */
    @Transactional
    @PreAuthorize(GET_EREIGNIS + " and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#wahlbezirkID, authentication)")
    public Optional<EreignisModel> getEreignis(@P("wahlbezirkID") final String wahlbezirkID) {
        log.info("#getEreignis");
        ereignisValidator.validWahlbezirkIDOrThrow(wahlbezirkID);
        return ereignisRepository.findById(wahlbezirkID).map(ereignisModelMapper::toModel);
    }

    /**
     * This BusinessAction's purpose is: Speichern von Ereignissen
     */
    @PreAuthorize(POST_EREIGNIS + " and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#param?.wahlbezirkID, authentication)")
    public void postEreignis(@P("param") EreignisModel ereignis) {
        log.info("#postEreignis");
        ereignisValidator.validEreignisAndWahlbezirkIDOrThrow(ereignis);
        try {
            ereignisRepository.save(ereignisModelMapper.toEntity(ereignis));
        } catch (Exception e) {
            log.error("postEreignis: Ereignis konnte nicht gespeichert werden. " + e);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.SAVEEREIGNIS_UNSAVABLE);
        }
    }
}
