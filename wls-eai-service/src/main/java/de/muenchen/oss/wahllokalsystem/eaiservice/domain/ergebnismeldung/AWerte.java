package de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class AWerte {

    @Column(name = "a1")
    private Long a1;

    @Column(name = "a2")
    private Long a2;

}
