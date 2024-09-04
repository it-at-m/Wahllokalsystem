package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisart;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record EreignisModel(@NotNull String wahlbezirkID,
                            String beschreibung,
                            java.time.LocalDateTime uhrzeit,
                            Ereignisart ereignisart) {
}
