package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class BedenklicherStimmzettel {

  @EmbeddedId
  @AttributeOverride(name = "wahlID", column = @Column(name = "fk_wahlid"))
  @AttributeOverride(name = "wahlbezirkID", column = @Column(name = "fk_wahlbezirkid"))
  @ToString.Include
  private BezirkIdWahlIdOrderIndex compositeId;

  @Convert(converter = SupplementsConverter.class)
  @ToString.Include
  private Set<Supplement> supplements;

  @MapsId("id")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumns({
    @JoinColumn(name = "fk_wahlid", referencedColumnName = "wahlid"),
    @JoinColumn(name = "fk_wahlbezirkid", referencedColumnName = "wahlbezirkid")
  })
  @EqualsAndHashCode.Exclude
  private BedenklicheStimmzettel erfassung;

  @Enumerated(EnumType.STRING)
  @ToString.Include
  Validity validity;
}
