package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelumschlaege;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record StimmzettelumschlaegeModel(@NotNull BezirkUndWahlID bezirkUndWahlID,
                                         LocalDateTime urneneroeffnungsUhrzeit,
                                         @NotNull long anzahlWaehler,
                                         long anzahlWaehler2) {
}
