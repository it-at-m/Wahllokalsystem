package de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class AWerte {

    private Long a1;
    
    private Long a2;

}
