package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlag;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Builder;

@Builder
public record WahlvorschlaegeModel(@NotNull BezirkUndWahlID bezirkUndWahlID,
                                   @NotNull String stimmzettelgebietID,
                                   @NotNull Set<WahlvorschlagModel> wahlvorschlaege) {

}
