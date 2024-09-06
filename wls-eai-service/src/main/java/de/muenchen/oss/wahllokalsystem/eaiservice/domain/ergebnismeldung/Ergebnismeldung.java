package de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.BaseEntity;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahlart;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Ergebnismeldung extends BaseEntity {

    @NotNull
    private String wahlbezirkID;

    @NotNull
    private String wahlID;

    private Meldungsart meldungsart;

    private AWerte aWerte;

    private BWerte bWerte;

    private WahlbriefeWerte wahlbriefeWerte;

    @ElementCollection
    @CollectionTable(name = "ungueltigeStimmzettel", joinColumns = @JoinColumn(name = "ungueltigeStimmzettelID"))
    private Set<UngueltigeStimmzettel> ungueltigeStimmzettels;

    private Long ungueltigeStimmzettelAnzahl;

    @ElementCollection
    @CollectionTable(name = "ergebnisse", joinColumns = @JoinColumn(name = "ergebnisseID"))
    private Set<Ergebnis> ergebnisse;

    private Wahlart wahlart;

}
