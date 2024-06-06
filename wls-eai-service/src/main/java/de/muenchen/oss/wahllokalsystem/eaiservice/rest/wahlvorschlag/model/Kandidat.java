package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record Kandidat(@NotNull String identifikator,
                       @NotNull String name,
                       @Schema(requiredMode = Schema.RequiredMode.REQUIRED) long listenposition,
                       boolean direktkandidat,
                       @Schema(requiredMode = Schema.RequiredMode.REQUIRED) long tabellenSpalteInNiederschrift,
                       boolean einzelbewerber) {
}
