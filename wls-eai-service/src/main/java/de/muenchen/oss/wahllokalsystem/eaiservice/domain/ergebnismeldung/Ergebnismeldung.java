package de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.BaseEntity;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahlart;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

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

    @Enumerated(EnumType.STRING)
    private Meldungsart meldungsart;

    @Embedded
    private AWerte aWerte;

    @Embedded
    private BWerte bWerte;

    @Embedded
    private WahlbriefeWerte wahlbriefeWerte;

    @ElementCollection
    @CollectionTable(name = "ungueltigeStimmzettel", joinColumns = @JoinColumn(name = "ergebnismeldungID"))
    private Set<UngueltigeStimmzettel> ungueltigeStimmzettels;

    private Long ungueltigeStimmzettelAnzahl;

    @ElementCollection
    @CollectionTable(name = "ergebnisse", joinColumns = @JoinColumn(name = "ergebnismeldungID"))
    private Set<Ergebnis> ergebnisse;

    @Enumerated(EnumType.STRING)
    private Wahlart wahlart;

    @CreationTimestamp
    private LocalDateTime erstellungszeit;

}
