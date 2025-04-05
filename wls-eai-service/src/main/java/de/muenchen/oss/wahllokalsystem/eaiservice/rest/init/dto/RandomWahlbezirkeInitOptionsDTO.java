package de.muenchen.oss.wahllokalsystem.eaiservice.rest.init.dto;

import jakarta.validation.constraints.NotNull;

public record RandomWahlbezirkeInitOptionsDTO(
        @NotNull RangeDTO uwb,
        @NotNull RangeDTO bwb,
        @NotNull RangeDTO a1,
        @NotNull RangeDTO a2,
        @NotNull RangeDTO a3
) {
}
