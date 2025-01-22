package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.ErgebnisseRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import jakarta.validation.constraints.NotNull;
import java.util.List;
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
public class ErgebnisseService {

    private final ErgebnisseRepository ergebnisseRepository;

    private final ErgebnisseModelMapper ergebnisseModelMapper;

    private final ErgebnisseValidator ergebnisseValidator;

    private final ExceptionFactory exceptionFactory;

    @PreAuthorize(
        "hasAuthority('Ergebnismeldung_BUSINESSACTION_GetErgebnisse')"
                + " and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#param.wahlbezirkID(), authentication)"
    )
    public Optional<ErgebnisseModel> getErgebnisse(@P("param") @NotNull final ErgebnisseReference ergebnisseReference) {
        log.info("#getErgebnisse");

        ergebnisseValidator.validReferenceOrThrow(ergebnisseReference,
                exceptionFactory.createFachlicheWlsException(ExceptionConstants.GET_ERGEBNISSE_PARAMETER_UNVOLLSTAENDIG));

        val ergebnisseFromRepo = ergebnisseRepository.findById(ergebnisseModelMapper.toEmbeddedId(ergebnisseReference));
        return ergebnisseFromRepo.map(ergebnisseModelMapper::toModel);
    }

    @PreAuthorize(
        "hasAuthority('Ergebnismeldung_BUSINESSACTION_GetErgebnisse')"
                + " and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#param.wahlbezirkID(), authentication)"
    )
    public List<ErgebnisseModel> getAllErgebnisse(@P("param") @NotNull final String wahlID, @NotNull final String wahlbezirkID) {
        log.info("#getErgebnisse");

        ergebnisseValidator.validIDOrThrow(wahlID, wahlbezirkID,
                exceptionFactory.createFachlicheWlsException(ExceptionConstants.GET_ERGEBNISSE_PARAMETER_UNVOLLSTAENDIG));

        val ergebnisseFromRepo = ergebnisseRepository.getAllErgebnisseInWahlbezirk(wahlID, wahlbezirkID);
        return ergebnisseFromRepo.stream().map(ergebnisseModelMapper::toModel).toList();
    }

    @PreAuthorize(
        "hasAuthority('Ergebnismeldung_BUSINESSACTION_PostErgebnisse')"
                + " and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#param.wahlbezirkID(), authentication)"
    )
    public void postErgebnisse(@P("param") final ErgebnisseReference ergebnisseReference, @NotNull final ErgebnisseModel ergebnisseToAdd) {
        log.info("#postErgebnisse");
        ergebnisseValidator.validModelOrThrow(ergebnisseToAdd);
        ergebnisseValidator.validReferenceOrThrow(ergebnisseReference,
                exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_ERGEBNISSE_PARAMETER_UNVOLLSTAENDIG));

        try {
            ergebnisseRepository.save(ergebnisseModelMapper.toEntity(ergebnisseToAdd));
        } catch (Exception e) {
            log.error("#postErgebnisse unsaveable:", e);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.ERGEBNISSE_UNSAVEABLE);

        }
    }
}
