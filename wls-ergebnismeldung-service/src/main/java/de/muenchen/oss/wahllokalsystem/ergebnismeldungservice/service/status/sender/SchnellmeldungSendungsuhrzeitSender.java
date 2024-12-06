package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.sender;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.StatusModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.ValidierungsstatusModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class SchnellmeldungSendungsuhrzeitSender extends AbstractStatusMonitoringSender {

    public SchnellmeldungSendungsuhrzeitSender(StatusClient monitoringClient) {
        super(monitoringClient);
    }

    @Override
    public void submitStatus(BezirkUndWahlID id, StatusModel newStatus, StatusModel oldStatus) {
        if (hasValidierungsstatusChanged(newStatus, oldStatus) || (oldStatus == null && isSchnellmeldungNotInvalid(newStatus))) {
            getMonitoringClient().postSchnellmeldungSendungsuhrzeit(id, LocalDateTime.now());
        }
    }

    private boolean hasValidierungsstatusChanged(final StatusModel newStatus, final StatusModel oldStatus) {
        if (oldStatus == null) {
            return false;
        }

        if (oldStatus.schnellmeldung() == null || newStatus.schnellmeldung() == null) {
            return false;
        }

        return newStatus.schnellmeldung().validierungsstatus() != null && oldStatus.schnellmeldung().validierungsstatus() != null
                && newStatus.schnellmeldung().validierungsstatus() != oldStatus.schnellmeldung().validierungsstatus();
    }

    private boolean isSchnellmeldungNotInvalid(final StatusModel statusModel) {
        return statusModel.schnellmeldung() != null && statusModel.schnellmeldung().validierungsstatus() != null
                && !ValidierungsstatusModel.NICHT_VALIDIERT.equals(statusModel.schnellmeldung().validierungsstatus());
    }

}
