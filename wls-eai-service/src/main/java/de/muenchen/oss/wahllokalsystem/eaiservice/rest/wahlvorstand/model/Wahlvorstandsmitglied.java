package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record Wahlvorstandsmitglied(@NotNull String identifikator,
                                    @NotNull String vorname,
                                    @NotNull String nachname,
                                    @NotNull String funktion,
                                    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean anwesend) {
}
