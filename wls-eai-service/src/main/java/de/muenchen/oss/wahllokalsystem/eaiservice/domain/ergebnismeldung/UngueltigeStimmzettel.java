package de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class UngueltigeStimmzettel {

    @Column(name = "stimmenart")
    private String stimmenart;


    @Column(name = "anzahl")
    private Long anzahl;


    @Column(name = "wahlvorschlagID")
    private String wahlvorschlagID;

}

