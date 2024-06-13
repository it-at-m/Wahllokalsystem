package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record WahlvorstandsmitgliedDTO(@NotNull String identifikator,
                                       @NotNull String vorname,
                                       @NotNull String nachname,
                                       @NotNull String funktion,
                                       @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean anwesend) {
}
