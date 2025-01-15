package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung.BezirkUndWahlIDStapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.Ergebnisse;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.ErgebnisseRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ErgebnisseService {

    private final ErgebnisseRepository ergebnisseRepository;

    private final ErgebnisseModelMapper ergebnisseModelMapper;

    private final ErgebnisseValidator ergebnisseValidator;

    private final ExceptionFactory exceptionFactory;

    @PreAuthorize(
            "hasAuthority('Ergebnismeldung_BUSINESSACTION_GetErgebnisse')"
                    + " and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#param.wahlbezirkID(), authentication)"
    )
    public ErgebnisseModel getErgebnisse(@P("param") @NotNull final ErgebnisseReference ergebnisseReference) {
        log.info("#getErgebnisse");
        ergebnisseValidator.validReferenceOrThrow(ergebnisseReference);

        BezirkUndWahlIDStapelart id = ergebnisseModelMapper.toEmbeddedId(ergebnisseReference);
        val ergebnisseFromRepo = getOrNull(id);
        return ergebnisseFromRepo == null ? null : ergebnisseModelMapper.toModel(ergebnisseFromRepo);
    }

    @PreAuthorize(
            "hasAuthority('Ergebnismeldung_BUSINESSACTION_PostErgebnisse')"
                    + " and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#param.wahlbezirkID(), authentication)"
    )
    public void postErgebnisse(@P("param") @NotNull ErgebnisseModel ergebnisseToAdd, ErgebnisseReference ergebnisseReference) {
        log.info("#postErgebnisse");
        ergebnisseValidator.validModelOrThrow(ergebnisseToAdd);
        ergebnisseValidator.validReferenceOrThrow(ergebnisseReference);

        try {
            ergebnisseRepository.save(ergebnisseModelMapper.toEntity(ergebnisseToAdd));
        } catch (Exception e) {
            log.info("Logged attribute: {}={}", "ergebnisse", ergebnisseToAdd);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.ERGEBNISSE_UNSAVEABLE);
        }
    }

    private Ergebnisse getOrNull(final BezirkUndWahlIDStapelart entityID) {
        return ergebnisseRepository.findById(entityID).orElse(null);
    }
}
