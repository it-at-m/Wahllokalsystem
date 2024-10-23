package de.muenchen.oss.wahllokalsystem.monitoringservice.rest.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record DruckdatenDTO(
        @NotNull BezirkUndWahlID bezirkUndWahlID,
        @NotNull LocalDateTime druckuhrzeit
) {
}
