package de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UngueltigeStimmzettel {

    @NotNull
    private String id;

    @Column(name = "stimmenart")
    private String stimmenart;

    @Column(name = "anzahl")
    private Long anzahl;

    @NotNull
    @Column(name = "wahlvorschlagID")
    private String wahlvorschlagID;

}

