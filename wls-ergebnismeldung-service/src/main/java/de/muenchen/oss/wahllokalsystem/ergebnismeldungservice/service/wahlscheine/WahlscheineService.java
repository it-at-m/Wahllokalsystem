package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.wahlscheine;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.wahlscheine.WahlscheineRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
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
public class WahlscheineService {

    private final WahlscheineRepository wahlscheineRepository;
    private final WahlscheineModelMapper wahlscheineModelMapper;
    private final WahlscheineValidator wahlscheineValidator;
    private final ExceptionFactory exceptionFactory;

    @PreAuthorize("hasAuthority('Ergebnismeldung_BUSINESSACTION_GetWahlscheine')")
    public Optional<WahlscheineModel> getWahlscheine(final BezirkUndWahlID id) {
        log.info("#getWahlscheine");

        wahlscheineValidator.validBezirkUndWahlIdOrThrow(id,
                exceptionFactory.createFachlicheWlsException(ExceptionConstants.GET_WAHLSCHEINE_PARAMETER_UNVOLLSTAENDIG));

        val wahlscheineFromRepo = wahlscheineRepository.findById(id);
        return wahlscheineFromRepo.map(wahlscheineModelMapper::toModel);
    }

    @PreAuthorize(
        "hasAuthority('Ergebnismeldung_BUSINESSACTION_PostWahlscheine')"
                + "and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#param?.getWahlbezirkID(), authentication)"
    )
    public void setWahlscheine(@P("param") final BezirkUndWahlID id, final WahlscheineModel wahlscheine) {
        log.info("#postWahlscheine");

        wahlscheineValidator.validBezirkUndWahlIdOrThrow(id,
                exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_WAHLSCHEINE_PARAMETER_UNVOLLSTAENDIG));
        wahlscheineValidator.validWahlscheineOrThrow(wahlscheine);

        try {
            wahlscheineRepository.save(wahlscheineModelMapper.toEntity(wahlscheine));
        } catch (Exception e) {
            log.error("#postWahlscheine unsaveable:", e);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.WAHLSCHEINE_UNSAVEABLE);
        }
    }
}
