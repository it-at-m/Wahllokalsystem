package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelumschlaege;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record StimmzettelumschlaegeDTO(@NotNull BezirkUndWahlID bezirkUndWahlID,
                                       LocalDateTime urneneroeffnungsUhrzeit,
                                       @NotNull long anzahlWaehler,
                                       Long anzahlWaehler2) {
}
