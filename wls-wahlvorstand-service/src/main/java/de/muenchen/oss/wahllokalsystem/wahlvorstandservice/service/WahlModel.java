package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service;


import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.model.Farbe;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.model.Wahlart;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record WahlModel(@NotNull String wahlID,
                        @NotNull String name,
                        @NotNull Long reihenfolge,
                        @NotNull Long waehlerverzeichnisnummer,
                        @NotNull LocalDate wahltag,
                        @NotNull Wahlart wahlart,
                        Farbe farbe,
                        String nummer) {
}