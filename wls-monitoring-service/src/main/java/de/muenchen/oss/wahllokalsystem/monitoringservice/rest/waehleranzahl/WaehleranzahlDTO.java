package de.muenchen.oss.wahllokalsystem.monitoringservice.rest.waehleranzahl;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record WaehleranzahlDTO(
    @NotNull String wahlID,
    @NotNull String wahlbezirkID,
    @NotNull Long anzahlWaehler,
    @NotNull LocalDateTime uhrzeit) {
}
