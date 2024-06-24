package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OrderColumn;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wahlvorschlaege {

    @EmbeddedId
    @NotNull
    private BezirkUndWahlID bezirkUndWahlID;

    @Column(name = "stimmzettelgebietID")
    @NotNull
    private String stimmzettelgebietID;

    @OrderColumn(name = "order_index")
    @JoinTable(name = "Wahlvorschlaege_Wahlvorschlaege", joinColumns = { @JoinColumn(name = "wahlvorschlaege_oid") })
    @ElementCollection
    @AttributeOverrides(
        {
                @AttributeOverride(name = "identifikator", column = @Column(name = "wahlvorschlaege_identifikator")),
                @AttributeOverride(name = "ordnungszahl", column = @Column(name = "wahlvorschlaege_ordnungszahl")),
                @AttributeOverride(name = "kurzname", column = @Column(name = "wahlvorschlaege_kurzname")),
                @AttributeOverride(name = "erhaeltStimmen", column = @Column(name = "wahlvorschlaege_erhaeltstimmen")),
                @AttributeOverride(name = "kandidaten", column = @Column(name = "wahlvorschlaege_kandidaten"))
        }
    )
    @NotNull
    @Size(min = 1)
    private java.util.Set<Wahlvorschlag> wahlvorschlaege = new java.util.LinkedHashSet<>();
}
