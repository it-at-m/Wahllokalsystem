package de.muenchen.oss.wahllokalsystem.eaiservice.rest.init.dto;

import jakarta.validation.constraints.NotNull;

public record RangeDTO(
        @NotNull int min,
        @NotNull int max
) {
}
