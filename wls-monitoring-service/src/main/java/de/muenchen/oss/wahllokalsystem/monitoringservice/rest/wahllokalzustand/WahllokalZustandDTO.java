package de.muenchen.oss.wahllokalsystem.monitoringservice.rest.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand.DruckzustandModel;
import java.time.LocalDateTime;
import java.util.Set;

public record WahllokalZustandDTO(
        String wahlID,
        String wahlbezirkID,
        LocalDateTime zuletztGesehen,
        LocalDateTime letzteAbmeldung,
        Set<DruckzustandModel> druckzustaende) {
}
