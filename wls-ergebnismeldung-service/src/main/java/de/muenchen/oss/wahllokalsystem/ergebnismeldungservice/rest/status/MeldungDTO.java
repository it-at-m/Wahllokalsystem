package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.status;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record MeldungDTO(
        @NotNull ValidierungsstatusDTO validierungsstatus,
        @NotNull boolean gedruckt,
        Boolean uebermittelt,
        LocalDateTime sendeuhrzeit
) {
}
