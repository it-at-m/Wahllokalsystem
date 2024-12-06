package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.sender;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.StatusModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.ValidierungsstatusModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class NiederschriftSendungsuhrzeitSender extends AbstractStatusMonitoringSender {

    public NiederschriftSendungsuhrzeitSender(StatusClient monitoringClient) {
        super(monitoringClient);
    }

    @Override
    public void submitStatus(BezirkUndWahlID id, StatusModel newStatus, StatusModel oldStatus) {
        if (hasValidierungsstatusChanged(newStatus, oldStatus) || (oldStatus == null && hasValidierungsstatusExceptNichtValidiert(newStatus))) {
            getMonitoringClient().postNiederschriftSendungsuhrzeit(id, LocalDateTime.now());
        }
    }

    private boolean hasValidierungsstatusChanged(final StatusModel newStatus, final StatusModel oldStatus) {
        if (oldStatus == null) {
            return false;
        }

        if (oldStatus.niederschrift() == null || newStatus.niederschrift() == null) {
            return false;
        }

        return oldStatus.niederschrift().validierungsstatus() != null && newStatus.niederschrift().validierungsstatus() != null
                && oldStatus.niederschrift().validierungsstatus() != newStatus.niederschrift().validierungsstatus();
    }

    private boolean hasValidierungsstatusExceptNichtValidiert(final StatusModel status) {
        return status.niederschrift() != null &&
                status.niederschrift().validierungsstatus() != null &&
                !status.niederschrift().validierungsstatus().equals(ValidierungsstatusModel.NICHT_VALIDIERT);
    }
}
