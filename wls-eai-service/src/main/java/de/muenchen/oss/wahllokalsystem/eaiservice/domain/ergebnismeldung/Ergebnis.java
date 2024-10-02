package de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ergebnis {

    @NotNull
    private String stimmenart;

    @NotNull
    private long wahlvorschlagsordnungszahl;

    @NotNull
    private long ergebnis;

    @NotNull
    private String wahlvorschlagID;

    @NotNull
    private String kandidatID;

}
