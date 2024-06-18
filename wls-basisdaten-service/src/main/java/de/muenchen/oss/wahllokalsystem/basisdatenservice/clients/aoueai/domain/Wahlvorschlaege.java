package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.aoueai.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OrderColumn;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wahlvorschlaege {

    @Column(name = "wahlID")
    @NotNull
    private String wahlID;

    @Column(name = "wahlbezirkID")
    @NotNull
    private String wahlbezirkID;


    @Column(name = "stimmzettelgebietID")
    @NotNull
    private String stimmzettelgebietID;


    @OrderColumn(name = "order_index")
    @JoinTable(name = "Wahlvorschlaege_Wahlvorschlaege", joinColumns = {@JoinColumn(name = "wahlvorschlaege_oid")})
    @ElementCollection
    @AttributeOverrides({
            @AttributeOverride(name = "identifikator", column = @Column(name = "wahlvorschlaege_identifikator")),
            @AttributeOverride(name = "ordnungszahl", column = @Column(name = "wahlvorschlaege_ordnungszahl")),
            @AttributeOverride(name = "kurzname", column = @Column(name = "wahlvorschlaege_kurzname")),
            @AttributeOverride(name = "erhaeltStimmen", column = @Column(name = "wahlvorschlaege_erhaeltstimmen")),
            @AttributeOverride(name = "kandidaten", column = @Column(name = "wahlvorschlaege_kandidaten"))
    })
    @NotNull
    @Size(min = 1)
    private java.util.Set<Wahlvorschlag> wahlvorschlaege = new java.util.LinkedHashSet<>();
}
