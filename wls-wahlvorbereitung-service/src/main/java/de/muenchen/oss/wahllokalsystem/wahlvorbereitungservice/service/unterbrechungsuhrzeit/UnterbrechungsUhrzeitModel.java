package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.unterbrechungsuhrzeit;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UnterbrechungsUhrzeitModel(@NotNull String wahlbezirkID, @NotNull LocalDateTime unterbrechungsUhrzeit) {
}
