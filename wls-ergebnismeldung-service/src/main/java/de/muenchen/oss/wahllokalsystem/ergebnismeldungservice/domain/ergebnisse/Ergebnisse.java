package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung.BezirkUndWahlIDStapelart;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ergebnisse {

    @EmbeddedId
    @JsonUnwrapped
    private BezirkUndWahlIDStapelart bezirkUndWahlIDStapelart;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "Ergebnissammlung", joinColumns = {
                    @JoinColumn(name = "fk_wahlid", referencedColumnName = "wahlid"),
                    @JoinColumn(name = "fk_wahlbezirkid", referencedColumnName = "wahlbezirkid"),
                    @JoinColumn(name = "fk_stapelart", referencedColumnName = "stapelart")
            }
    )
    @NotNull
    @Size(min = 1)
    @Builder.Default
    private java.util.List<Ergebnis> ergebnisse = new java.util.ArrayList<>();
}
