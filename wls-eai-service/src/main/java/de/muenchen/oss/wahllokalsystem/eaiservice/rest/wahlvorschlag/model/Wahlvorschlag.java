package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record Wahlvorschlag(@NotNull String identifikator,
                            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) long ordnungszahl,
                            @NotNull String kurzname,
                            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean erhaeltStimmen,
                            Set<Kandidat> kandidaten) {
}
