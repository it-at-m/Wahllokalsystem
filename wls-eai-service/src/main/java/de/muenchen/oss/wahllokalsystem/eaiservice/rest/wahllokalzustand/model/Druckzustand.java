package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.model;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record Druckzustand(@NotNull String wahlID,
                           @NotNull LocalDateTime schnellmeldungSendenUhrzeit,
                           @NotNull LocalDateTime niederschriftSendenUhrzeit,
                           @NotNull LocalDateTime schnellmeldungDruckUhrzeit,
                           @NotNull LocalDateTime niederschriftDruckUhrzeit
) {
}
