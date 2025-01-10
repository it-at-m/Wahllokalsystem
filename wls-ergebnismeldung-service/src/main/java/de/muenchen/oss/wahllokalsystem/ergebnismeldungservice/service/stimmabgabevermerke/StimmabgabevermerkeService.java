package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.StimmabgabevermerkeRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
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
public class StimmabgabevermerkeService {

    private final StimmabgabevermerkeModelMapper stimmabgabevermerkeModelMapper;
    private final StimmabgabevermerkeRepository stimmabgabevermerkeRepository;
    private final StimmabgabevermerkeValidator stimmabgabevermerkeValidator;
    private final ExceptionFactory exceptionFactory;

    @PreAuthorize("hasAuthority('Ergebnismeldung_BUSINESSACTION_GetStimmabgabevermerke')")
    public Optional<StimmabgabevermerkeModel> getStimmabgabevermerke(final BezirkIDUndWaehlerverzeichnisNummer id) {
        log.info("#getStimmabgabevermerke");
        stimmabgabevermerkeValidator.validBezirkIDUndWaehlerverzeichnisnummerOrThrow(id, exceptionFactory.createFachlicheWlsException(ExceptionConstants.GET_STIMMABGABEVERMERKE_PARAMETER_UNVOLLSTAENDIG));
        val savFromRepository = stimmabgabevermerkeRepository.findById(id);
        return savFromRepository.map(stimmabgabevermerkeModelMapper::toModel);
    }

    @PreAuthorize(
        "hasAuthority('Ergebnismeldung_BUSINESSACTION_PostStimmabgabevermerke')"
                + "and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#param?.getWahlbezirkID(), authentication)"
    )
    public void postStimmabgabevermerke(@P("param") final BezirkIDUndWaehlerverzeichnisNummer id, final StimmabgabevermerkeModel stimmabgabevermerkeModel) {
        log.info("#postStimmabgabevermerke");
        stimmabgabevermerkeValidator.validBezirkIDUndWaehlerverzeichnisnummerOrThrow(id, exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_STIMMABGABEVERMERKE_PARAMETER_UNVOLLSTAENDIG));
        try{
            stimmabgabevermerkeRepository.save(stimmabgabevermerkeModelMapper.toEntity(stimmabgabevermerkeModel));
        } catch(Exception e){
            log.error("#postStimmabgabevermerke unsaveable:", e);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.STIMMABGABEVERMERKE_UNSAVEABLE);
        }
    }
}
