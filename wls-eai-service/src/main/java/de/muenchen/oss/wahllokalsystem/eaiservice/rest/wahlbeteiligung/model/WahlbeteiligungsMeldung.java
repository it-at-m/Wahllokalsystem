package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlbeteiligung.model;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record WahlbeteiligungsMeldung(@NotNull String wahlID,
                                      @NotNull String wahlbezirkID,
                                      long anzahlWaehler,
                                      @NotNull LocalDateTime meldeZeitpunkt) {
}
