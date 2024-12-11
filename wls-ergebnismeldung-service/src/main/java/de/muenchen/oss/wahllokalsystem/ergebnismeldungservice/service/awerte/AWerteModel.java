package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AWerteModel(@NotNull BezirkUndWahlID bezirkUndWahlID,
                          @NotNull long a1,
                          Long a2) {
}
