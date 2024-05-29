package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.model;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record Wahlbezirk(@NotNull String identifikator,
                         @NotNull WahlbezirkArt wahlbezirkArt,
                         @NotNull String nummer,
                         @NotNull LocalDate wahltag,
                         @NotNull String wahlnummer,
                         @NotNull String wahlID) {
}
