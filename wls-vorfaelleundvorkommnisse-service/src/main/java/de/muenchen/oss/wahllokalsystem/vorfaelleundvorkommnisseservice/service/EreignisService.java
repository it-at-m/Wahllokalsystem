package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.EreignisRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class EreignisService {

    private final EreignisRepository ereignisRepository;
    private final ExceptionFactory exceptionFactory;
    private final EreignisModelMapper ereignisModelMapper;
    private final EreignisValidator ereignisValidator;

    @PreAuthorize(
        "hasAuthority('VorfaelleUndVorkommnisse_BUSINESSACTION_GetEreignisse')"
                + " and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#wahlbezirkID, authentication)"
    )
    public Optional<WahlbezirkEreignisseModel> getEreignisse(@P("wahlbezirkID") final String wahlbezirkID) {
        log.info("#getEreignis");
        ereignisValidator.validWahlbezirkIDOrThrow(wahlbezirkID);

        val ereignisModelListe = ereignisRepository.findByWahlbezirkID(wahlbezirkID).stream().map(ereignisModelMapper::toModel).toList();

        if (ereignisModelListe.isEmpty()) {
            return Optional.empty();
        } else {
            val keineVorfaelle = ereignisModelListe.stream().noneMatch(ereignis -> EreignisartModel.VORFALL.equals(ereignis.ereignisart()));
            val keineVorkommnisse = ereignisModelListe.stream().noneMatch(ereignis -> EreignisartModel.VORKOMMNIS.equals(ereignis.ereignisart()));

            val ereignisseModel = ereignisModelMapper.toEreignisseModel(wahlbezirkID, keineVorfaelle, keineVorkommnisse, ereignisModelListe);
            return Optional.of(ereignisseModel);
        }
    }

    @Transactional
    @PreAuthorize(
        "hasAuthority('VorfaelleUndVorkommnisse_BUSINESSACTION_PostEreignisse')"
                + " and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#param?.wahlbezirkID, authentication)"
    )
    public void postEreignisse(@P("param") EreignisseWriteModel ereignisse) {
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
