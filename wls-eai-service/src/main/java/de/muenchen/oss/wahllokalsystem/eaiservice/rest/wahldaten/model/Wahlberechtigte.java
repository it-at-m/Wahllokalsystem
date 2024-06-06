package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record Wahlberechtigte(@NotNull String wahlID,
                              @NotNull String wahlbezirkID,
                              @Schema(requiredMode = Schema.RequiredMode.REQUIRED) long a1,
                              @Schema(requiredMode = Schema.RequiredMode.REQUIRED) long a2,
                              @Schema(requiredMode = Schema.RequiredMode.REQUIRED) long a3) {
}
