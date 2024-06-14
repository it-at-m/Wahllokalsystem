package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.eroeffnungsuhrzeit;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record EroeffnungsUhrzeitDTO(@NotNull String wahlbezirkID, @NotNull LocalDateTime eroeffnungsuhrzeit) {
}
