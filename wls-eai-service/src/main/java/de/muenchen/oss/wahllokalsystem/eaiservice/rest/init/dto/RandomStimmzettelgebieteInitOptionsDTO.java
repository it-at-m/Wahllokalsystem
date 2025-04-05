package de.muenchen.oss.wahllokalsystem.eaiservice.rest.init.dto;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.StimmzettelgebietsartDTO;
import jakarta.validation.constraints.NotNull;

public record RandomStimmzettelgebieteInitOptionsDTO(
        @NotNull RangeDTO count,
        @NotNull StimmzettelgebietsartDTO stimmzettelgebietsart,
        RandomWahlbezirkeInitOptionsDTO randomWahlbezirke,
        @NotNull WahlvorschlagInitOptions wahlvorschlagInitOptions
) {
}
