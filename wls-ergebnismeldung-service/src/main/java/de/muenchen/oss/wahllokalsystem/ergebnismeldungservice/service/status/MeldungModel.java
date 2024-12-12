package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record MeldungModel(
        @NotNull ValidierungsstatusModel validierungsstatus,
        @NotNull boolean gedruckt,
        Boolean uebermittelt,
        LocalDateTime sendeuhrzeit
) {
}
