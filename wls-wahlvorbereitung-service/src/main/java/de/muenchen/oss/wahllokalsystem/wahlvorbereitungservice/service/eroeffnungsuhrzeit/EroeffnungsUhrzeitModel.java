package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.eroeffnungsuhrzeit;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record EroeffnungsUhrzeitModel(@NotNull String wahlbezirkID, @NotNull LocalDateTime eroeffnungsuhrzeit) {
}
