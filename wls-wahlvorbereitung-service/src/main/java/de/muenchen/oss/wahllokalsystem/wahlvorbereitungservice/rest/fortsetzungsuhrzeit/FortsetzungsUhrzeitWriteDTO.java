package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.fortsetzungsuhrzeit;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record FortsetzungsUhrzeitWriteDTO(@NotNull LocalDateTime fortsetzungsUhrzeit) {
}
