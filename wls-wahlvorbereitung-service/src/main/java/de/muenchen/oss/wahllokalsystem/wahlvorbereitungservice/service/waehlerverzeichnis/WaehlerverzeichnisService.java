package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.waehlerverzeichnis;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.WaehlerverzeichnisRepository;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import jakarta.validation.constraints.NotNull;
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
public class WaehlerverzeichnisService {

    private final WaehlerverzeichnisRepository waehlerverzeichnisRepository;

    private final WaehlerverzeichnisValidator waehlerverzeichnisValidator;

    private final WaehlerverzeichnisModelMapper waehlerverzeichnisModelMapper;

    private final ExceptionFactory exceptionFactory;

    @PreAuthorize(
        "hasAuthority('Wahlvorbereitung_BUSINESSACTION_GetWaehlerverzeichnis')"
                + "and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#ref.wahlbezirkID, authentication)"
    )
    public Optional<WaehlerverzeichnisModel> getWaehlerverzeichnis(@P("ref") final BezirkIDUndWaehlerverzeichnisNummer waehlerverzeichnisReference) {
        log.debug("#getWaehlerverzeichnis");
        log.debug("in: wahlbezirkID & wvzNummer > {}", waehlerverzeichnisReference);

        waehlerverzeichnisValidator.validWaehlerverzeichnisReferenceOrThrow(waehlerverzeichnisReference);

        val waehlerverzeichnisFromRepo = waehlerverzeichnisRepository.findById(waehlerverzeichnisReference).map(waehlerverzeichnisModelMapper::toModel);
        log.debug("out: waehlerverzeichnis > {}", waehlerverzeichnisFromRepo.orElse(null));

        return waehlerverzeichnisFromRepo;
    }

    @PreAuthorize("hasAuthority('Wahlvorbereitung_BUSINESSACTION_PostWaehlerverzeichnis')")
    public void setWaehlerverzeichnis(@NotNull final WaehlerverzeichnisModel waehlververzeichnisToSet) {
        waehlerverzeichnisValidator.validModelToSetOrThrow(waehlververzeichnisToSet);

        try {
            waehlerverzeichnisRepository.save(waehlerverzeichnisModelMapper.toEntity(waehlververzeichnisToSet));
        } catch (final Exception onSaveException) {
            log.error("Fehler beim speichern: ", onSaveException);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.UNSAVEABLE);
        }
    }
}
