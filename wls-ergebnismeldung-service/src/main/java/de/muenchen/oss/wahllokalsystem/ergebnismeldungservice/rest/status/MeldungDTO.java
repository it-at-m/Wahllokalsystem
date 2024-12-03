package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.status;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.status.Validierungsstatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record MeldungDTO(
        @NotNull Validierungsstatus validierungsstatus,
        @NotNull boolean gedruckt,
        Boolean uebermittelt,
        LocalDateTime sendeuhrzeit
) {
}
