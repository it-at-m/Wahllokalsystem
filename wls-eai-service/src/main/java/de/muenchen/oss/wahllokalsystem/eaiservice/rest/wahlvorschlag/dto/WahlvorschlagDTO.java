package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record WahlvorschlagDTO(@NotNull String identifikator,
                               @Schema(requiredMode = Schema.RequiredMode.REQUIRED) long ordnungszahl,
                               @NotNull String kurzname,
                               @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean erhaeltStimmen,
                               Set<KandidatDTO> kandidaten) {
}
