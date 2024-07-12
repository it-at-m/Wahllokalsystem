package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record BasisstrukturdatenDTO(@NotNull String wahlID,
                                    @NotNull String stimmzettelgebietID,
                                    @NotNull String wahlbezirkID,
                                    @NotNull LocalDate wahltag) {
}
