package de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record WaehleranzahlModel(
    @NotNull BezirkUndWahlID bezirkUndWahlID,
    @NotNull Long anzahlWaehler,
    @NotNull LocalDateTime uhrzeit) {
}
