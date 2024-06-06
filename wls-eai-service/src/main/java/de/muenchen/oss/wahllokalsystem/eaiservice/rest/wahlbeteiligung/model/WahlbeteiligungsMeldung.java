package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlbeteiligung.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record WahlbeteiligungsMeldung(@NotNull String wahlID,
                                      @NotNull String wahlbezirkID,
                                      @Schema(requiredMode = Schema.RequiredMode.REQUIRED) long anzahlWaehler,
                                      @NotNull LocalDateTime meldeZeitpunkt) {
}
