package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.EreignisRepository;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisart;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @PreAuthorize(GET_EREIGNIS + " and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#wahlbezirkID, authentication)")
    public Optional<EreignisseModel> getEreignis(@P("wahlbezirkID") final String wahlbezirkID) {
        log.info("#getEreignis");
        ereignisValidator.validWahlbezirkIDOrThrow(wahlbezirkID);

        val ereignisModelListe = ereignisRepository.findByWahlbezirkID(wahlbezirkID).stream().map(ereignisModelMapper::toModel).toList();

        if (ereignisModelListe.isEmpty()) {
            return Optional.empty();
        } else {
            val keineVorfaelle = ereignisModelListe.stream().noneMatch(ereignis -> Ereignisart.VORFALL.equals(ereignis.ereignisart()));
            val keineVorkommnisse = ereignisModelListe.stream().noneMatch(ereignis -> Ereignisart.VORKOMMNIS.equals(ereignis.ereignisart()));

            val ereignisseModel = ereignisModelMapper.toEreignisseModel(wahlbezirkID, keineVorfaelle, keineVorkommnisse, ereignisModelListe);
            return Optional.of(ereignisseModel);
        }
    }

    /**
     * This BusinessAction's purpose is: Speichern von Ereignissen
     */
    @Transactional
    @PreAuthorize(POST_EREIGNIS + " and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#param?.wahlbezirkID, authentication)")
    public void postEreignis(@P("param") EreignisseWriteModel ereignisse) {
        log.info("#postEreignis");
        ereignisValidator.validEreignisAndWahlbezirkIDOrThrow(ereignisse);
        try {
            ereignisRepository.deleteByWahlbezirkID(ereignisse.wahlbezirkID());
            ereignisRepository.saveAll(ereignisModelMapper.toEntity(ereignisse));
        } catch (Exception e) {
            log.error("postEreignis: Ereignis konnte nicht gespeichert werden. " + e);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.SAVEEREIGNIS_UNSAVABLE);
        }
    }
}
