package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.model;

import jakarta.validation.constraints.NotNull;

public record WahlvorstandsmitgliedAktualisierung(@NotNull String identifikator,
                                                  boolean anwesend) {
}
