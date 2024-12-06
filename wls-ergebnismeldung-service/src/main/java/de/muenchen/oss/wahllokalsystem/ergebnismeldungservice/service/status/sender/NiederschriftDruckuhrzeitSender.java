package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.sender;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.StatusModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class NiederschriftDruckuhrzeitSender extends AbstractStatusMonitoringSender {

    public NiederschriftDruckuhrzeitSender(StatusClient monitoringClient) {
        super(monitoringClient);
    }

    @Override
    public void submitStatus(BezirkUndWahlID id, StatusModel newStatus, StatusModel oldStatus) {
        if (hasDruckuhrzeitChanged(newStatus, oldStatus) || (oldStatus == null && isGedruckt(newStatus))) {
            getMonitoringClient().postNiederschriftDruckuhrzeit(id, LocalDateTime.now());
        }
    }

    private boolean hasDruckuhrzeitChanged(final StatusModel newStatus, final StatusModel oldStatus) {
        if (oldStatus == null) {
            return false;
        }

        return oldStatus.niederschrift().gedruckt() != newStatus.niederschrift().gedruckt();
    }

    private boolean isGedruckt(final StatusModel status) {
        return status.niederschrift() != null && status.niederschrift().gedruckt();
    }
}
