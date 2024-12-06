package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.sender;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.StatusModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.ValidierungsstatusModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import io.micrometer.tracing.annotation.DefaultNewSpanParser;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class SchnellmeldungSendungsuhrzeitSender extends AbstractStatusMonitoringSender {

    private final DefaultNewSpanParser newSpanParser;

    public SchnellmeldungSendungsuhrzeitSender(StatusClient monitoringClient, DefaultNewSpanParser newSpanParser) {
        super(monitoringClient);
        this.newSpanParser = newSpanParser;
    }

    @Override
    public void submitStatus(BezirkUndWahlID id, StatusModel newStatus, StatusModel oldStatus) {
        if (hasValidierungsstatusChange(newStatus, oldStatus) || (oldStatus == null && isSchnellmeldungNotInvalid(newStatus))) {
            getMonitoringClient().postSchnellmeldungSendungsuhrzeit(id, LocalDateTime.now());
        }
    }

    private boolean hasValidierungsstatusChange(final StatusModel newStatus, final StatusModel oldStatus) {
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
