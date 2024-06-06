package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record Referendumvorlage(@NotNull String wahlvorschlagID,
                                @Schema(requiredMode = Schema.RequiredMode.REQUIRED) long ordnungszahl,
                                @NotNull String kurzname,
                                @NotNull String frage,
                                @NotNull @Size(min = 1) Set<Referendumoption> referendumoption) {
}
