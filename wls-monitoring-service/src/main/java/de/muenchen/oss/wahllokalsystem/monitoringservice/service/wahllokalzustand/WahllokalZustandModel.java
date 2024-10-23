package de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Builder;

@Builder
public record WahllokalZustandModel(
        String wahlID,
        String wahlbezirkID,
        LocalDateTime zuletztGesehen,
        LocalDateTime letzteAbmeldung,
        Set<DruckzustandModel> druckzustaende) {
}
