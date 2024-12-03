package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.status.StatusRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.status.StatusClient;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
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
public class StatusService {

    private final StatusRepository statusRepository;
    private final StatusModelMapper statusModelMapper;
    private final StatusValidator statusValidator;
    private final ExceptionFactory exceptionFactory;
    private final StatusClient monitoringClient;

    @PreAuthorize("hasAuthority('Ergebnismeldung_BUSINESSACTION_GetStatus')")
    public Optional<StatusModel> getStatus(final BezirkUndWahlID id) {
        log.info("#getStatus");

        statusValidator.valideBezirkUndWahlIdOrThrow(id,
                exceptionFactory.createFachlicheWlsException(ExceptionConstants.GET_STATUS_PARAMETER_UNVOLLSTAENDIG));

        val statusFromRepo = statusRepository.findById(id);
        return statusFromRepo.map(statusModelMapper::toModel);
    }

    @PreAuthorize(
        "hasAuthority('Ergebnismeldung_BUSINESSACTION_PostStatus')"
                + "and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#param?.wahlbezirkID(), authentication)"
    )
    public void setStatus(@P("param") final BezirkUndWahlID id, final StatusModel status) {
        log.info("#postStatus");

        statusValidator.valideBezirkUndWahlIdOrThrow(id,
                exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_STATUS_PARAMETER_UNVOLLSTAENDIG));
        statusValidator.valideStatusOrThrow(status);

        val lastStatus = statusRepository.findById(id).map(statusModelMapper::toModel);
        if (lastStatus.isPresent()) {
            val schnellmeldung = lastStatus.get().schnellmeldung();
            val niederschrift = lastStatus.get().niederschrift();
            if (schnellmeldung.validierungsstatus() != null && status.schnellmeldung().validierungsstatus() != null
                    && schnellmeldung.validierungsstatus() != status.schnellmeldung().validierungsstatus()) {
                monitoringClient.postSchnellmeldungSendungsuhrzeit(id, LocalDateTime.now());
            }
            if (schnellmeldung.gedruckt() != status.schnellmeldung().gedruckt()) {
                monitoringClient.postSchnellmeldungDruckuhrzeit(id, LocalDateTime.now());
            }
            if (niederschrift.validierungsstatus() != null && status.niederschrift().validierungsstatus() != null
                    && niederschrift.validierungsstatus() != status.niederschrift().validierungsstatus()) {
                monitoringClient.postNiederschriftSendungsuhrzeit(id, LocalDateTime.now());
            }
            if (niederschrift.gedruckt() != status.niederschrift().gedruckt()) {
                monitoringClient.postNiederschriftDruckuhrzeit(id, LocalDateTime.now());
            }
        } else {
            if (status.schnellmeldung() != null &&
                    status.schnellmeldung().validierungsstatus() != null &&
                    !status.schnellmeldung().validierungsstatus().equals(ValidierungsstatusModel.NICHT_VALIDIERT)) {
                monitoringClient.postSchnellmeldungSendungsuhrzeit(id, LocalDateTime.now());
            }
            if (status.schnellmeldung() != null && status.schnellmeldung().gedruckt()) {
                monitoringClient.postSchnellmeldungDruckuhrzeit(id, LocalDateTime.now());
            }
            if (status.niederschrift() != null &&
                    status.niederschrift().validierungsstatus() != null &&
                    !status.niederschrift().validierungsstatus().equals(ValidierungsstatusModel.NICHT_VALIDIERT)) {
                monitoringClient.postNiederschriftSendungsuhrzeit(id, LocalDateTime.now());
            }
            if (status.niederschrift() != null && status.niederschrift().gedruckt()) {
                monitoringClient.postNiederschriftDruckuhrzeit(id, LocalDateTime.now());
            }
        }

        try {
            statusRepository.save(statusModelMapper.toEntity(status));
        } catch (Exception e) {
            log.error("#postStatus unsaveable:", e);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.STATUS_UNSAVEABLE);
        }
    }
}
