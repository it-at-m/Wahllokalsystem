package de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class WahlbriefeWerte {

    @Column(name = "zurueckgewiesenGesamt")
    private Long zurueckgewiesenGesamt;

}
