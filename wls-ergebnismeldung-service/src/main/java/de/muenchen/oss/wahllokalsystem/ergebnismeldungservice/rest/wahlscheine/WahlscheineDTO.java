package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.wahlscheine;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.validation.constraints.NotNull;

public record WahlscheineDTO (@NotNull BezirkUndWahlID bezirkUndWahlID,
                              @NotNull Long stimmabgabevermerke) {
}
