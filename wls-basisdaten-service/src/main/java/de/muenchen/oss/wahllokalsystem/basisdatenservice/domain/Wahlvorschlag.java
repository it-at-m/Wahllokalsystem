package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OrderColumn;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wahlvorschlag {

    @Column(name = "identifikator")
    @NotNull
    private String identifikator;

    @Column(name = "ordnungszahl")
    @NotNull
    private long ordnungszahl;

    @Column(name = "kurzname")
    @NotNull
    private String kurzname;

    @Column(name = "erhaeltStimmen")
    @NotNull
    private boolean erhaeltStimmen;

    @OrderColumn(name = "order_index")
    @JoinTable(name = "Wahlvorschlag_Kandidaten", joinColumns = { @JoinColumn(name = "wahlvorschlag_oid") })
    @ElementCollection
    @AttributeOverrides(
        {
                @AttributeOverride(name = "identifikator", column = @Column(name = "kandidaten_identifikator")),
                @AttributeOverride(name = "name", column = @Column(name = "kandidaten_name")),
                @AttributeOverride(name = "listenposition", column = @Column(name = "kandidaten_listenposition")),
                @AttributeOverride(name = "direktkandidat", column = @Column(name = "kandidaten_direktkandidat")),
                @AttributeOverride(name = "tabellenSpalteInNiederschrift", column = @Column(name = "kandidaten_tabellenspalteinniederschrift")),
                @AttributeOverride(name = "einzelbewerber", column = @Column(name = "kandidaten_einzelbewerber"))
        }
    )
    private java.util.Set<Kandidat> kandidaten = new java.util.LinkedHashSet<>();
}
