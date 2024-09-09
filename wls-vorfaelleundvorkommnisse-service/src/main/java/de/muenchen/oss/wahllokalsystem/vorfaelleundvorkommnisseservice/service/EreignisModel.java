package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisart;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record EreignisModel(@NotNull String wahlbezirkID,
                            String beschreibung,
                            LocalDateTime uhrzeit,
                            Ereignisart ereignisart) {
}
