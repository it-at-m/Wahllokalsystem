package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record WahlvorstandsmitgliedAktualisierung(@NotNull String identifikator,
                                                  @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean anwesend) {
}
