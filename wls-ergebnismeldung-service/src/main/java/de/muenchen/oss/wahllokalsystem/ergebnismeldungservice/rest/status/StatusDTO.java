package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.status;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.validation.constraints.NotNull;

public record StatusDTO(
        @NotNull BezirkUndWahlID bezirkUndWahlID,
        @NotNull MeldungDTO schnellmeldung,
        @NotNull MeldungDTO niederschrift
) {
}
