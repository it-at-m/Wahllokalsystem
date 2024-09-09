package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisart;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record EreignisDTO(@NotNull String wahlbezirkID,
                          String beschreibung,
                          LocalDateTime uhrzeit,
                          Ereignisart ereignisart) {
}
