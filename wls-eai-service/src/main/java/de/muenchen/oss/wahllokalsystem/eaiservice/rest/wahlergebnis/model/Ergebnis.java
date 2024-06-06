package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record Ergebnis(@NotNull String stimmenart,
                       @Schema(requiredMode = Schema.RequiredMode.REQUIRED) long wahlvorschlagsordnungszahl,
                       @Schema(requiredMode = Schema.RequiredMode.REQUIRED) long ergebnis,
                       String wahlvorschlagID,
                       String kandidatID
) {
}
