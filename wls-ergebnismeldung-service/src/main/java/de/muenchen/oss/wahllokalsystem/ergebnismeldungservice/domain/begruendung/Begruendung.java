package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Begruendung {

    @EmbeddedId
    private BezirkUndWahlIDStapelart bezirkUndWahlIDStapelart;

    @NotNull
    @Size(max = 1024)
    private String grund1;

    @Size(max = 1024)
    private String grund2;

    @NotNull
    private boolean nachzaehlung;

    @NotNull
    private boolean unstimmigkeiten;

}
