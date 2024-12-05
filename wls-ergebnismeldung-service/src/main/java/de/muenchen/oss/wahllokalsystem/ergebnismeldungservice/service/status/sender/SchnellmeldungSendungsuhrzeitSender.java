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
    public void submitStatus(BezirkUndWahlID id, StatusModel statusModel, StatusModel oldStatus) {
        if (hasValidierungsstatusChange(statusModel, oldStatus) || isSchnellmeldungNotInvalid(statusModel)) {
            getMonitoringClient().postSchnellmeldungSendungsuhrzeit(id, LocalDateTime.now());
        }
    }

    private boolean hasValidierungsstatusChange(final StatusModel statusModel, final StatusModel oldStatus) {
        if (oldStatus == null) {
            return false;
        }
        return statusModel.schnellmeldung().validierungsstatus() != null && oldStatus.schnellmeldung().validierungsstatus() != null
                && statusModel.schnellmeldung().validierungsstatus() != oldStatus.schnellmeldung().validierungsstatus();
    }

    private boolean isSchnellmeldungNotInvalid(final StatusModel statusModel) {
        return statusModel.schnellmeldung() != null &&
                !ValidierungsstatusModel.NICHT_VALIDIERT.equals(statusModel.schnellmeldung().validierungsstatus());
    }

}
