package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.urnenwahlschliessungsuhrzeit;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UrnenwahlSchliessungsUhrzeitWriteDTO(@NotNull LocalDateTime urnenwahlSchliessungsUhrzeit) {
}
