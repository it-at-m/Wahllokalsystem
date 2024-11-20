package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.awerte;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AWerteDTO(
    @NotNull String wahlID,
    @NotNull String wahlbezirkID,
    @NotNull long a1,
    Long a2) {
}
