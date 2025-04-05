package de.muenchen.oss.wahllokalsystem.eaiservice.rest.init.dto;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.StimmzettelgebietsartDTO;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record StimmzettelgebietInitOptionsDTO(
        String nummer,
        String name,
        @NotNull StimmzettelgebietsartDTO stimmzettelgebietsart,
        RandomWahlbezirkeInitOptionsDTO randomWahlbezirke,
        List<WahlbezirkOptionsDTO> wahlbezirkOptions,
        @NotNull WahlvorschlagInitOptions wahlvorschlagInitOptions
) {
}
