package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettel;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;

@Getter
@Setter
@Entity
@Table(name = "WaehlerStimmzettel")
@ToString(onlyExplicitlyIncluded = true)
public class Stimmzettel {

  @NaturalId @EmbeddedId @ToString.Include private BezirkIDWahlIDNummer combinedId;

  @Convert(converter = IntArrayToStringConverter.class)
  @NotNull private List<Integer> selectedWahlvorschlaegeOrdnungszahlen;

  @ElementCollection
  @CollectionTable(
      name = "Stimmzettel_Kandidat",
      joinColumns = {
        @JoinColumn(name = "fk_wahlbezirkID", referencedColumnName = "wahlbezirkID"),
        @JoinColumn(name = "fk_wahlID", referencedColumnName = "wahlID"),
        @JoinColumn(name = "fk_stimmzettelNummer", referencedColumnName = "stimmzettelNummer")
      })
  private List<StimmzettelKandidat> kandidaten;
}
