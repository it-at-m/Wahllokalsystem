package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.fortsetzungsuhrzeit;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record FortsetzungsUhrzeitDTO(@NotNull String wahlbezirkID,
                                     @NotNull LocalDateTime fortsetzungsUhrzeit) {
}
