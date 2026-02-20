package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record DruckzustandDTO(
    @NotNull String wahlID,
    LocalDateTime schnellmeldungSendenUhrzeit,
    LocalDateTime niederschriftSendenUhrzeit,
    LocalDateTime schnellmeldungDruckUhrzeit,
    LocalDateTime niederschriftDruckUhrzeit) {}
