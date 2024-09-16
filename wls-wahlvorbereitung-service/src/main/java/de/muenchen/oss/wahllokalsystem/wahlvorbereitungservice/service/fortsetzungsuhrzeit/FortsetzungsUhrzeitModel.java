package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.fortsetzungsuhrzeit;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record FortsetzungsUhrzeitModel(@NotNull String wahlbezirkID, @NotNull LocalDateTime fortsetzungsUhrzeit) {
}
