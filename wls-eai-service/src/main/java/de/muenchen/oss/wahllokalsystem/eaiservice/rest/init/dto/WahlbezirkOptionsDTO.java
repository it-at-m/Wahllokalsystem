package de.muenchen.oss.wahllokalsystem.eaiservice.rest.init.dto;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlbezirkArtDTO;
import jakarta.validation.constraints.NotNull;

public record WahlbezirkOptionsDTO(
        @NotNull WahlbezirkArtDTO wahlbezirkArt,
        @NotNull String nummer,
        long a1,
        long a2,
        long a3
) {
}
