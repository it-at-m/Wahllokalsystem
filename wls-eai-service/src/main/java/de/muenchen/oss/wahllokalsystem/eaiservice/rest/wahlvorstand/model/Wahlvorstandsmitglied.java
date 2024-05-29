package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.model;

import jakarta.validation.constraints.NotNull;

public record Wahlvorstandsmitglied(@NotNull String identifikator,
                                    @NotNull String vorname,
                                    @NotNull String nachname,
                                    @NotNull String funktion,
                                    boolean anwesend) {
}
