package de.muenchen.oss.wahllokalsystem.monitoringservice.rest.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record SendungsdatenDTO(
        @NotNull BezirkUndWahlID bezirkUndWahlID,
        @NotNull LocalDateTime sendungsuhrzeit) {
}
