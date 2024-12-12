package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.status.StatusRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmabgabevermerke;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.StimmabgabevermerkeRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.StatusModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.StatusModelMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.StatusValidator;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.sender.AbstractStatusMonitoringSender;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.models.StimmabgabevermerkeModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
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
public class StimmabgabevermerkeService {

    private final StimmabgabevermerkeModelMapper stimmabgabevermerkeModelMapper;
    private final StimmabgabevermerkeRepository stimmabgabevermerkeRepository;
    private final StimmabgabevermerkeValidator stimmabgabevermerkeValidator;
    private final ExceptionFactory exceptionFactory;

    //
//    private final StatusModelMapper statusModelMapper;

//    private final ExceptionFactory exceptionFactory;
//    private final List<AbstractStatusMonitoringSender> monitoringSender;

    @PreAuthorize("hasAuthority('Ergebnismeldung_BUSINESSACTION_GetStimmabgabevermerke')")
    public Optional<StimmabgabevermerkeModel> getStimmabgabevermerke(final BezirkIDUndWaehlerverzeichnisNummer id) {
        log.info("#getStimmabgabevermerke");
        stimmabgabevermerkeValidator.validBezirkIDUndWaehlerverzeichnisnummerOrThrow(id, exceptionFactory.createFachlicheWlsException(ExceptionConstants.GET_STIMMABGABEVERMERKE_PARAMETER_UNVOLLSTAENDIG));
        val savFromRepository = stimmabgabevermerkeRepository.findById(id);
        return savFromRepository.map(stimmabgabevermerkeModelMapper::toModel);
    }

    @PreAuthorize(
        "hasAuthority('Ergebnismeldung_BUSINESSACTION_PostStatus')"
                + "and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#param?.getWahlbezirkID(), authentication)"
    )
    public void setStatus(@P("param") final BezirkUndWahlID id, final StatusModel status) {
//        log.info("#postStatus");
//
//        statusValidator.validBezirkUndWahlIdOrThrow(id,
//                exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_STATUS_PARAMETER_UNVOLLSTAENDIG));
//        statusValidator.validStatusOrThrow(status);
//
//        val lastStatus = statusRepository.findById(id).map(statusModelMapper::toModel);
//        monitoringSender.forEach(sender -> sender.submitStatus(id, status, lastStatus.orElse(null)));
//
//        try {
//            statusRepository.save(statusModelMapper.toEntity(status));
//        } catch (Exception e) {
//            log.error("#postStatus unsaveable:", e);
//            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.STATUS_UNSAVEABLE);
//        }
    }
}
