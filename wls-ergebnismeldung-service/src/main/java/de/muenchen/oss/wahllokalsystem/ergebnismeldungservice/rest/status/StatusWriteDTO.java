package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.status;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.monitoring.model.BezirkUndWahlID;
import jakarta.validation.constraints.NotNull;

public record StatusWriteDTO(
        @NotNull BezirkUndWahlID bezirkUndWahlID,
        @NotNull MeldungDTO schnellmeldung,
        @NotNull MeldungDTO niederschrift
) {
}
