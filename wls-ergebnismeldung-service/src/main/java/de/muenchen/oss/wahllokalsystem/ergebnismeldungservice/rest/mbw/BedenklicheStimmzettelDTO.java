package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.mbw;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record BedenklicheStimmzettelDTO(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) int orderIndex,
    Set<SupplementDTO> supplements,
    @NotNull ValidityDTO validity) {}
