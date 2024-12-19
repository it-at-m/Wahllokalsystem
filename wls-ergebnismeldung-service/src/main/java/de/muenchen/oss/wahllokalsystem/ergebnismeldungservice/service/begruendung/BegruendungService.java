package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.begruendung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.Begruendung;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.BegruendungRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.BezirkUndWahlIDStapelart;
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
public class BegruendungService {

    private final BegruendungRepository begruendungRepository;

    private final BegruendungModelMapper begruendungModelMapper;

    private final BegruendungValidator begruendungValidator;

    private final ExceptionFactory exceptionFactory;

    @PreAuthorize(
        "hasAuthority('Ergebnismeldung_BUSINESSACTION_GetBegruendung')"
                + " and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#param.wahlbezirkID(), authentication)"
    )
    public BegruendungModel getBegruendung(@P("param") @NotNull final BegruendungReference begruendungReference) {
        log.info("#getBegruendung");
        begruendungValidator.validReferenceOrThrow(begruendungReference);

        BezirkUndWahlIDStapelart id = begruendungModelMapper.toEmbeddedId(begruendungReference);
        val begruendungFromRepo = getOrNull(id);
        return begruendungFromRepo == null ? null : begruendungModelMapper.toModel(begruendungFromRepo);
    }

    @PreAuthorize(
        "hasAuthority('Ergebnismeldung_BUSINESSACTION_PostBegruendung')"
                + " and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#param.wahlbezirkID(), authentication)"
    )
    public void setBegruendung(@P("param") @NotNull BegruendungModel begruendungToAdd) {
        log.info("#postBegruendung");
        begruendungValidator.validModelOrThrow(begruendungToAdd);

        try {
            begruendungRepository.save(begruendungModelMapper.toEntity(begruendungToAdd));
        } catch (Exception e) {
            log.error("#postStatus unsaveable:", e);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.STATUS_UNSAVEABLE);
        }
    }

    private Begruendung getOrNull(final BezirkUndWahlIDStapelart entityID) {
        return begruendungRepository.findById(entityID).orElse(null);
    }
}
