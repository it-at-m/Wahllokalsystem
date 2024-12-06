package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.sender;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.StatusModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class SchnellmeldungDruckuhrzeitSender extends AbstractStatusMonitoringSender {

    public SchnellmeldungDruckuhrzeitSender(StatusClient monitoringClient) {
        super(monitoringClient);
    }

    @Override
    public void submitStatus(BezirkUndWahlID id, StatusModel newStatus, StatusModel oldStatus) {
        if (hasGedruckedChanged(newStatus, oldStatus) || (oldStatus == null && isGedruckt(newStatus))) {
            getMonitoringClient().postSchnellmeldungDruckuhrzeit(id, LocalDateTime.now());
        }
    }

    private boolean hasGedruckedChanged(final StatusModel newStatus, final StatusModel oldStatus) {
        if (oldStatus == null) {
            return false;
        }

        return newStatus.schnellmeldung().gedruckt() != oldStatus.schnellmeldung().gedruckt();
    }

    private boolean isGedruckt(final StatusModel status) {
        return status.schnellmeldung() != null && status.schnellmeldung().gedruckt();
    }
}
