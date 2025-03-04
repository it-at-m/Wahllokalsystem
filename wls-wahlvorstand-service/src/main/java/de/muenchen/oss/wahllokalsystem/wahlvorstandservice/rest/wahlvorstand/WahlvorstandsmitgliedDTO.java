package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.rest.wahlvorstand;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record WahlvorstandsmitgliedDTO(@NotNull String identifikator,
                                       @NotNull String familienname,
                                       @NotNull String vorname,
                                       @NotNull FunktionDTO funktion,
                                       String funktionsname,
                                       @NotNull boolean anwesend) {
}
