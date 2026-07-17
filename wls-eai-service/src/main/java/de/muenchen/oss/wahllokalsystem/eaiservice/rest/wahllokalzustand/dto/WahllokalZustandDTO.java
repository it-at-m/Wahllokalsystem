package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

public record WahllokalZustandDTO(
    @NotNull String wahlbezirkID,
    @NotNull String teamID,
    LocalDateTime zuletztGesehen,
    LocalDateTime letzteAbmeldung,
    Set<DruckzustandDTO> druckzustaende) {}
