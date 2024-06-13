package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record DruckzustandDTO(@NotNull String wahlID,
                              @NotNull LocalDateTime schnellmeldungSendenUhrzeit,
                              @NotNull LocalDateTime niederschriftSendenUhrzeit,
                              @NotNull LocalDateTime schnellmeldungDruckUhrzeit,
                              @NotNull LocalDateTime niederschriftDruckUhrzeit
) {
}
