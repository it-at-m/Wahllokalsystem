package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record KandidatDTO(@NotNull String identifikator,
                          @NotNull String name,
                          @Schema(requiredMode = Schema.RequiredMode.REQUIRED) long listenposition,
                          @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean direktkandidat,
                          @Schema(requiredMode = Schema.RequiredMode.REQUIRED) long tabellenSpalteInNiederschrift,
                          @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean einzelbewerber) {
}
