package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service;

import com.google.common.base.Strings;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.EreignisRepository;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

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

    /**
     * This BusinessAction's purpose is: Laden der Ereignisse It returns one Ereignis.
     */
    @Transactional
    @PreAuthorize("hasAuthority('VorfaelleUndVorkommnisse_BUSINESSACTION_GetEreignisse')" + "and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#wahlbezirkID, authentication)")
    public Optional<EreignisModel> getEreignis(@P("wahlbezirkID") final String wahlbezirkID) {
        log.info("#getEreignis");
        if (Strings.isNullOrEmpty(wahlbezirkID)) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETEREIGNIS_SUCHKRITERIEN_UNVOLLSTAENDIG);
        }
        return ereignisRepository.findById(wahlbezirkID).map(ereignisModelMapper::toModel);
    }

    /**
     * This BusinessAction's purpose is: Speichern von Ereignissen
     */
    @PreAuthorize("hasAuthority('VorfaelleUndVorkommnisse_BUSINESSACTION_PostEreignisse')" + "and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#param?.wahlbezirkID, authentication)")
    public void postEreignis(@P("param") EreignisModel ereignis) {
        log.info("#postEreignis");
        if (ereignis == null || Strings.isNullOrEmpty(ereignis.wahlbezirkID())) {
           log.warn("#postEreignis: Parameter unvollständig");
           throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTEREIGNIS_PARAMS_UNVOLLSTAENDIG);
        }

        try {
            ereignisRepository.save(ereignisModelMapper.toEntity(ereignis));
        } catch (Exception e) {
            log.error("postEreignis: Ereignis konnte nicht gespeichert werden. " + e);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.SAVEEREIGNIS_UNSAVABLE);
        }
    }
}
