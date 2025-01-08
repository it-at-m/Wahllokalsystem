package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelumschlaege;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.common.security.AuthenticationHandler;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelumschlaege.StimmzettelumschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StimmzettelumschlaegeService {

    private static final String WAHLBEZIRK_ART_USER_DETAIL_KEY = "wahlbezirksArt";

    private final StimmzettelumschlaegeRepository stimmzettelumschlaegeRepository;
    private final StimmzettelumschlaegeModelMapper stimmzettelumschlaegeModelMapper;
    private final StimmzettelumschlaegeValidator stimmzettelumschlaegeValidator;
    private final ExceptionFactory exceptionFactory;
    private final Collection<AuthenticationHandler> authenticationHandlers;

    @PreAuthorize("hasAuthority('Ergebnismeldung_BUSINESSACTION_GetStimmzettelumschlaege')")
    public Optional<StimmzettelumschlaegeModel> getStimmzettelumschlaege(final BezirkUndWahlID id) {
        log.info("#getStimmzettelumschlaege");

        stimmzettelumschlaegeValidator.validBezirkUndWahlIdOrThrow(id,
                exceptionFactory.createFachlicheWlsException(ExceptionConstants.GET_STIMMZETTELUMSCHLAEGE_PARAMETER_UNVOLLSTAENDIG));

        val stimmzettelumschlaegeFromRepo = stimmzettelumschlaegeRepository.findById(id);
        return stimmzettelumschlaegeFromRepo.map(stimmzettelumschlaegeModelMapper::toModel);
    }

    @PreAuthorize(
        "hasAuthority('Ergebnismeldung_BUSINESSACTION_PostStimmzettelumschlaege')"
                + "and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#param?.getWahlbezirkID(), authentication)"
    )
    public void setStimmzettelumschlaege(@P("param") final BezirkUndWahlID id, final StimmzettelumschlaegeModel stimmzettelumschlaege) {
        log.info("#postStimmzettelumschlaege");

        stimmzettelumschlaegeValidator.validBezirkUndWahlIdOrThrow(id,
                exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_STIMMZETTELUMSCHLAEGE_PARAMETER_UNVOLLSTAENDIG));
        stimmzettelumschlaegeValidator.validStimmzettelumschlaegeOrThrow(stimmzettelumschlaege);
        val wahlbezirkArtOfRequest = getWahlbezirkArtOfCurrentAuthentication();
        stimmzettelumschlaegeValidator.validHasBWBRequiredEroeffnungsUhrzeitOrThrow(wahlbezirkArtOfRequest, stimmzettelumschlaege.urneneroeffnungsUhrzeit());

        try {
            stimmzettelumschlaegeRepository.save(stimmzettelumschlaegeModelMapper.toEntity(stimmzettelumschlaege));
        } catch (Exception e) {
            log.error("#postStimmzettelumschlaege unsaveable:", e);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.STIMMZETTELUMSCHLAEGE_UNSAVEABLE);
        }
    }

    private WahlbezirkArtModel getWahlbezirkArtOfCurrentAuthentication() {
        val currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        val authenticationHandler = authenticationHandlers.stream().filter(handler -> handler.canHandle(currentAuthentication)).findFirst();
        try {
            val wahlbezirkOfUser = authenticationHandler.get().getDetail(WAHLBEZIRK_ART_USER_DETAIL_KEY, currentAuthentication);
            return wahlbezirkOfUser.map(WahlbezirkArtModel::valueOf).get();
        } catch (Exception e) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.WAHLBEZIRKART_NOT_LOADABLE);
        }
    }
}
