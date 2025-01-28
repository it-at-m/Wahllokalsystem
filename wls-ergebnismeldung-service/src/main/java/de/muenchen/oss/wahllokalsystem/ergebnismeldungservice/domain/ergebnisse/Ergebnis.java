package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ergebnis {

    @Size(max = 1024)
    private String wahlvorschlagID;

    @Size(max = 1024)
    private String kandidatID;

    private Long wahlvorschlagsordnungszahl;

    @NotNull
    private long ergebnis;

    private Long numIndex;
}
