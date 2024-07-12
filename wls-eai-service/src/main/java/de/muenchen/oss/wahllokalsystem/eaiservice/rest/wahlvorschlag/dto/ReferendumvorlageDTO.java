package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record ReferendumvorlageDTO(@NotNull String wahlvorschlagID,
                                   @Schema(requiredMode = Schema.RequiredMode.REQUIRED) long ordnungszahl,
                                   @NotNull String kurzname,
                                   @NotNull String frage,
                                   @NotNull @Size(min = 1) Set<ReferendumoptionDTO> referendumoptionen) {
}
