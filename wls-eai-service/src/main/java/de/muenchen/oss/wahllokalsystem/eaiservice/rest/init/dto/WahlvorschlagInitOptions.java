package de.muenchen.oss.wahllokalsystem.eaiservice.rest.init.dto;

import jakarta.validation.constraints.NotNull;

public record WahlvorschlagInitOptions(
        @NotNull Integer wahlvorschlaegeMinCount,
        @NotNull Integer wahlvorschlaegeMaxCount,
        @NotNull Integer kandidatenMinCount,
        @NotNull Integer kandidatenMaxCount
) {
}
