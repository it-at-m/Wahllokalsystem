package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record WahlurneDTO(@NotNull @Size(max = 255) String wahlID,
                          @Schema(requiredMode = Schema.RequiredMode.REQUIRED) long anzahl,
                          Boolean urneVersiegelt) {
}
