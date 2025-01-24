package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.AusdruckRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndMeldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class AusdruckService {

    private final AusdruckRepository ausdruckRepository;
    private final AusdruckModelMapper ausdruckModelMapper;
    private final ExceptionFactory exceptionFactory;
    private final WahlUndBezirkIDUndMeldungsartValidator wahlUndBezirkIDUndMeldungsartValidator;
    private final Validator validator;

    @PreAuthorize("hasAuthority('Ergebnismeldung_BUSINESSACTION_GetAusdruck')")
    public List<AusdruckModel> getAllAusdrucke(@NotBlank final String wahlID, @NotBlank final String wahlbezirkID) {
        val ausdrucke = ausdruckRepository.findByWahlIdAndWahlbezirkId(wahlID, wahlbezirkID);

        return ausdrucke.stream().map(ausdruckModelMapper::toModel).toList();
    }

    @PreAuthorize(
        "hasAuthority('Ergebnismeldung_BUSINESSACTION_PostAusdruck') and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#param?.wahlUndBezirkIDUndMeldungsart.wahlbezirkID, authentication)"
    )
    public void saveAusdruck(@P("param") @NotNull final AusdruckModel ausdruck) {
        log.debug("Saving printout {}", ausdruck.wahlUndBezirkIDUndMeldungsart().getMeldungsart());

        if (!validator.validate(ausdruck).isEmpty()) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_AUSDRUCK_PARAMETER_UNVOLLSTAENDIG);
        }

        ausdruckRepository.save(ausdruckModelMapper.toEntity(ausdruck, Instant.now()));
        log.info("Saved printout: {}", ausdruck);
    }

    @PreAuthorize("hasAuthority('Ergebnismeldung_BUSINESSACTION_GetAusdruck')")
    public Optional<AusdruckModel> getAusdruck(WahlUndBezirkIDUndMeldungsart id) {
        log.debug("Loading printout {}", id.getMeldungsart());

        wahlUndBezirkIDUndMeldungsartValidator.validWahlUndBezirkIDUndMeldungsartOrThrow(id,
                exceptionFactory.createFachlicheWlsException(ExceptionConstants.GET_AUSDRUCK_PARAMETER_UNVOLLSTAENDIG));

        val result = ausdruckRepository.findById(id);

        if (result.isEmpty()) {
            log.info("Printout not found for: {}", id);
        }

        return result.map(ausdruckModelMapper::toModel);
    }
}
