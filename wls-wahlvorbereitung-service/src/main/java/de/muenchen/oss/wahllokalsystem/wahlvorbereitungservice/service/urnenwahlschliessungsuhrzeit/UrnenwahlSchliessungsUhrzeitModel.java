package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlschliessungsuhrzeit;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UrnenwahlSchliessungsUhrzeitModel(@NotNull String wahlbezirkID, @NotNull LocalDateTime schliessungsuhrzeit) {

}
