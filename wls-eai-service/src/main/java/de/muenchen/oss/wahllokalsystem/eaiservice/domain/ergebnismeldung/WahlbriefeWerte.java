package de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class WahlbriefeWerte {

    private Long zurueckgewiesenGesamt;

}
