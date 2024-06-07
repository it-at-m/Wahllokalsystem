package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record WahlvorstandsmitgliedAktualisierungDTO(@NotNull String identifikator,
                                                     @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean anwesend) {
}
