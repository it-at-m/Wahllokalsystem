package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
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
public class Stimmzettel {

  @EmbeddedId @ToString.Include @EqualsAndHashCode.Include private StimmzettelID id;

  @Convert(converter = IntArrayToStringConverter.class)
  @NotNull private List<Integer> selectedWahlvorschlaegeOrdnungszahlen;

  @ElementCollection
  @CollectionTable(
      name = "Stimmzettel_Kandidat",
      joinColumns = {
        @JoinColumn(name = "fk_wahlbezirkID", referencedColumnName = "wahlbezirkID"),
        @JoinColumn(name = "fk_wahlID", referencedColumnName = "wahlID"),
        @JoinColumn(name = "fk_teamID", referencedColumnName = "teamID"),
        @JoinColumn(name = "fk_stimmzettelkennung", referencedColumnName = "stimmzettelkennung")
      })
  private List<StimmzettelKandidat> kandidaten;
}
