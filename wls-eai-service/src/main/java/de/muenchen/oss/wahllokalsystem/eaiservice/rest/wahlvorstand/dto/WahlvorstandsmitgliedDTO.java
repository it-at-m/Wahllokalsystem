package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record WahlvorstandsmitgliedDTO(@NotNull String identifikator,
                                       @NotNull String vorname,
                                       @NotNull String nachname,
                                       @NotNull WahlvorstandFunktionDTO funktion,
                                       @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean anwesend) {
}
