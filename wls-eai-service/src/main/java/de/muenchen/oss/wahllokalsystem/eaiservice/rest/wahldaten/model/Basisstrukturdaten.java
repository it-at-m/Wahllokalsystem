package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.model;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record Basisstrukturdaten(@NotNull String wahlID,
                                 @NotNull String stimmzettelgebietID,
                                 @NotNull String wahlbezirkID,
                                 @NotNull LocalDate wahltag) {
}
