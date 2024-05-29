package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

public record WahllokalZustand(@NotNull String wahlbezirkID,
                               @NotNull LocalDateTime zuletztGesehen,
                               @NotNull LocalDateTime letzteAbmeldung,
                               @NotNull @Size(min = 1) Set<Druckzustand> druckzustaende) {
}
