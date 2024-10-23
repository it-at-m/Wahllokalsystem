package de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record DruckzustandModel(@NotNull String wahlID,
                                @NotNull LocalDateTime schnellmeldungSendenUhrzeit,
                                @NotNull LocalDateTime niederschriftSendenUhrzeit,
                                @NotNull LocalDateTime schnellmeldungDruckUhrzeit,
                                @NotNull LocalDateTime niederschriftDruckUhrzeit) {
}
