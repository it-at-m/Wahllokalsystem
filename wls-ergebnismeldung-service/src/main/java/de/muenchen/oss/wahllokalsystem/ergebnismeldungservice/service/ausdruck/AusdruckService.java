package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Ausdruck;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.AusdruckRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndMeldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public List<AusdruckModel> getAll(@NotBlank final String wahlID, @NotBlank final String wahlbezirkID) {
        return ausdruckModelMapper.toModelList(
                ausdruckRepository.findAllByWahlUndBezirkIDUndMeldungsart_WahlIDAndWahlUndBezirkIDUndMeldungsart_WahlbezirkID(wahlID, wahlbezirkID));
    }

    @PreAuthorize(
        "hasAuthority('Ergebnismeldung_BUSINESSACTION_PostAusdruck') and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#param?.wahlUndBezirkIDUndMeldungsart.wahlbezirkID, authentication)"
    )
    public void saveAusdruck(@P("param") @NotNull final AusdruckModel ausdruck) {
        log.debug("Saving printout {}", ausdruck.wahlUndBezirkIDUndMeldungsart().getMeldungsart());

        if (!validator.validate(ausdruck).isEmpty()) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.CODE_SAVEAUSDRUCK_PARAMETER_UNVOLLSTAENDIG);
        }

        ausdruckRepository.save(ausdruckModelMapper.toEntity(ausdruck));
        log.info("Saved printout: {}", ausdruck);
    }

    @PreAuthorize("hasAuthority('Ergebnismeldung_BUSINESSACTION_GetAusdruck')")
    public AusdruckModel getAusdruck(WahlUndBezirkIDUndMeldungsart wahlUndBezirkIDUndMeldungsart) {
        log.debug("Loading printout {}", wahlUndBezirkIDUndMeldungsart.getMeldungsart());

        wahlUndBezirkIDUndMeldungsartValidator.validWahlUndBezirkIDUndMeldungsartOrThrow(wahlUndBezirkIDUndMeldungsart);

        Ausdruck result = ausdruckRepository.findOneByWahlUndBezirkIDUndMeldungsart(wahlUndBezirkIDUndMeldungsart);

        if (result == null) {
            log.info("Printout not found for: {}", wahlUndBezirkIDUndMeldungsart);
        }
        return ausdruckModelMapper.toModel(result);
    }
}
