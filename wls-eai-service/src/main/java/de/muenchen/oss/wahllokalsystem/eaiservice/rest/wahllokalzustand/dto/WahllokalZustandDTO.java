package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

public record WahllokalZustandDTO(@NotNull String wahlbezirkID,LocalDateTime zuletztGesehen,LocalDateTime letzteAbmeldung,Set<DruckzustandDTO>druckzustaende){}
