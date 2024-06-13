package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

public record WahllokalZustandDTO(@NotNull String wahlbezirkID,
                                  @NotNull LocalDateTime zuletztGesehen,
                                  @NotNull LocalDateTime letzteAbmeldung,
                                  @NotNull @Size(min = 1) Set<DruckzustandDTO> druckzustaende) {
}
