package de.muenchen.oss.wahllokalsystem.wlsgraphqlservice.controller.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record Wahlbezirk(
        @NotNull UUID id,
        @NotNull LocalDate wahltagDate,
        @NotNull int nummer,
        @NotNull WahlbezirkArt art
) {
}
