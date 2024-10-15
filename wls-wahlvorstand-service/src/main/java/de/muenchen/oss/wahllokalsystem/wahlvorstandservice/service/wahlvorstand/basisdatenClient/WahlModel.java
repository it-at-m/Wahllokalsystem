package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.basisdatenClient;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record WahlModel(@NotNull String wahlID,
                        @NotNull String name,
                        @NotNull Long reihenfolge,
                        @NotNull Long waehlerverzeichnisnummer,
                        @NotNull LocalDate wahltag,
                        @NotNull Wahlart wahlart,
                        Farbe farbe,
                        String nummer) {
}
