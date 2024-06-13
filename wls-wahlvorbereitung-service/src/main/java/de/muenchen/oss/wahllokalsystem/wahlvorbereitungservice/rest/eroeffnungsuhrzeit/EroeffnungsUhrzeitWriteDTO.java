package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.eroeffnungsuhrzeit;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record EroeffnungsUhrzeitWriteDTO(@NotNull LocalDateTime eroeffnungsuhrzeit) {

}
