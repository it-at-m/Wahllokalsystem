package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record WahlbezirkDTO(@NotNull String identifikator,
                            @NotNull WahlbezirkArtDTO wahlbezirkArt,
                            @NotNull String nummer,
                            @NotNull LocalDate wahltag,
                            @NotNull String wahlnummer,
                            @NotNull String wahlID) {
}
