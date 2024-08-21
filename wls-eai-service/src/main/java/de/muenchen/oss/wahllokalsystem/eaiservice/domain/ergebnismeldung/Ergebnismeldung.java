package de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.BaseEntity;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahlart;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Ergebnismeldung extends BaseEntity {

    @Column(name = "wahlbezirkID")
    @NotNull
    private String wahlbezirkID;


    @Column(name = "wahlID")
    @NotNull
    private String wahlID;


    @Column(name = "meldungsart")
    @Enumerated(EnumType.STRING)
    private Meldungsart meldungsart;


    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "a1", column = @Column(name = "awerte_a1")),
            @AttributeOverride(name = "a2", column = @Column(name = "awerte_a2"))
    })
    private AWerte aWerte;


    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "b", column = @Column(name = "bwerte_b")),
            @AttributeOverride(name = "b1", column = @Column(name = "bwerte_b1")),
            @AttributeOverride(name = "b2", column = @Column(name = "bwerte_b2"))
    })
    private BWerte bWerte;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "zurueckgewiesenGesamt", column = @Column(name = "wahlbriefewerte_zurueckgewiesenGesamt"))
    })
    private WahlbriefeWerte wahlbriefeWerte;


    @OrderColumn(name = "order_index")
    @JoinTable(name = "Ergebnismeldung_UngueltigeStimmzettels", joinColumns = {@JoinColumn(name = "ergebnismeldung_oid")})
    @ElementCollection
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "stimmenart", column = @Column(name = "ungueltigestimmzettels_stimmenart")),
            @AttributeOverride(name = "anzahl", column = @Column(name = "ungueltigestimmzettels_anzahl")),
            @AttributeOverride(name = "wahlvorschlagID", column = @Column(name = "ungueltigestimmzettels_wahlvorschlagid"))
    })
    private List<UngueltigeStimmzettel> ungueltigeStimmzettels = new java.util.ArrayList<>();


    @Column(name = "ungueltigeStimmzettelAnzahl")
    private Long ungueltigeStimmzettelAnzahl;


    @OrderColumn(name = "order_index")
    @JoinTable(name = "Ergebnismeldung_Ergebnisse", joinColumns = {@JoinColumn(name = "ergebnismeldung_oid")})
    @ElementCollection
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "stimmenart", column = @Column(name = "ergebnisse_stimmenart")),
            @AttributeOverride(name = "wahlvorschlagsordnungszahl", column = @Column(name = "ergebnisse_wahlvorschlagsordnungszahl")),
            @AttributeOverride(name = "ergebnis", column = @Column(name = "ergebnisse_ergebnis")),
            @AttributeOverride(name = "wahlvorschlagID", column = @Column(name = "ergebnisse_wahlvorschlagid")),
            @AttributeOverride(name = "kandidatID", column = @Column(name = "ergebnisse_kandidatid"))
    })
    @OneToMany
    private List<Ergebnis> ergebnisse = new java.util.ArrayList<>();

    private Wahlart wahlart;


}
