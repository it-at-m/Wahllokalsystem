package de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@Embeddable
public class BWerte {

    @Column(name = "b")
    private Long b;


    @Column(name = "b1")
    private Long b1;


    @Column(name = "b2")
    private Long b2;

}
