package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DSEStimmzettel {

  @EmbeddedId @ToString.Include @EqualsAndHashCode.Include private StimmzettelID id;

  @NotNull private boolean isValid;

  @NotNull private int invalideVotes;

  @NotNull private StimmzettelGueltigkeit gueltigkeit;

  @OneToMany(mappedBy = "stimmzettel")
  private List<DSEBeschlussvormerkung> beschlussvormerkungen;

  @Embedded private Beschlussfassung beschlussfassung;

  @OneToMany(mappedBy = "stimmzettel")
  private List<DSEWahlvorschlag> wahlvorschlaege;
}
