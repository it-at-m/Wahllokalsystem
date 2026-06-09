package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw;

import jakarta.persistence.Convert;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BedenklicheStimmzettel {

  @EmbeddedId private BezirkIdWahlIdOrderIndex compositeId;

  @Convert(converter = SupplementsConverter.class)
  private Set<Supplement> supplements;

  @Enumerated(EnumType.STRING)
  Validity validity;
}
