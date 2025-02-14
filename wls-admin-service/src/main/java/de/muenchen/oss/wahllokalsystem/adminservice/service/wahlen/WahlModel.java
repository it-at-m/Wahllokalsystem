package de.muenchen.oss.wahllokalsystem.adminservice.service.wahlen;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record WahlModel(@NotNull String wahlID,
                        @NotNull String name,
                        @NotNull Long reihenfolge,
                        @NotNull Long waehlerverzeichnisnummer,
                        @NotNull LocalDate wahltag,
                        @NotNull WahlartModel wahlart,
                        FarbeModel farbe) {
}
