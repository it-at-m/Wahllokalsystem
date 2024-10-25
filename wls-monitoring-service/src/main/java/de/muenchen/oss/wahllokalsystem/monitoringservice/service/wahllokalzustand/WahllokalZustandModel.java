package de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.Builder;

@Builder
public record WahllokalZustandModel(
        String wahlbezirkID,
        LocalDateTime zuletztGesehen,
        LocalDateTime letzteAbmeldung,
        Set<DruckzustandModel> druckzustaende) {
}
