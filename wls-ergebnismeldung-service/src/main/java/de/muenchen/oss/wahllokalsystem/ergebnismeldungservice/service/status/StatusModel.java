package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.validation.constraints.NotNull;

public record StatusModel(
        @NotNull BezirkUndWahlID bezirkUndWahlID,
        @NotNull MeldungModel schnellmeldung,
        @NotNull MeldungModel niederschrift) {
}
