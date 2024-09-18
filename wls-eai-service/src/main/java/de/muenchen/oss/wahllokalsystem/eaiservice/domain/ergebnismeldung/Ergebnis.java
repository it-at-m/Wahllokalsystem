package de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Ergebnis {

    @Column(name = "stimmenart")
    @NotNull
    private String stimmenart;


    @Column(name = "wahlvorschlagsordnungszahl")
    @NotNull
    private long wahlvorschlagsordnungszahl;


    @Column(name = "ergebnis")
    @NotNull
    private long ergebnis;


    @Column(name = "wahlvorschlagID")
    @NotNull
    private String wahlvorschlagID;


    @Column(name = "kandidatID")
    @NotNull
    private String kandidatID;

}
