package de.muenchen.oss.wahllokalsystem.eaiservice.rest.ergebnismeldung.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ErgebnisDTO(@NotNull String stimmenart,
                          @Schema(requiredMode = Schema.RequiredMode.REQUIRED) long wahlvorschlagsordnungszahl,
                          @Schema(requiredMode = Schema.RequiredMode.REQUIRED) long ergebnis,
                          @NotNull String wahlvorschlagID,
                          @NotNull String kandidatID
) {
}
