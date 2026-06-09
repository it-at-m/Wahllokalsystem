package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw;

import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BedenklicherStimmzettel {
  private int orderIndex;

  @Convert(converter = SupplementsConverter.class)
  private Set<Supplement> supplements;

  @Enumerated(EnumType.STRING)
  Validity validity;
}
