package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.wahlscheine;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.validation.constraints.NotNull;

public record WahlscheineModel(@NotNull BezirkUndWahlID bezirkUndWahlID,
                               @NotNull Long stimmabgabevermerke){
}
