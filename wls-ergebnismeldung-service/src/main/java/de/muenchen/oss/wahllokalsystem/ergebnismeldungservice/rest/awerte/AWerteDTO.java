package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.awerte;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AWerteDTO(
    @NotNull BezirkUndWahlID bezirkUndWahlID,
    @NotNull long a1,
    Long a2) {
}
