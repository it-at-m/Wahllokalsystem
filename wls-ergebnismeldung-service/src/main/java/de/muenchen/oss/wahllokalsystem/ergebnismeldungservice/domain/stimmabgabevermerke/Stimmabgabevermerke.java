package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Stimmabgabevermerke {

  @EmbeddedId @NotNull @ToString.Include
  private BezirkUndWahlIDUndWaehlerverzeichnisnummer bezirkUndWahlIDUndWaehlerverzeichnisnummer;

  @OneToMany(
      mappedBy = "stimmabgabevermerke",
      orphanRemoval = true,
      cascade = CascadeType.ALL,
      fetch = FetchType.EAGER)
  @NotNull private Set<Vermerk> vermerke = new LinkedHashSet<>();

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "EingenommeneWahlscheine",
      joinColumns = {
        @JoinColumn(name = "fk_wahlbezirkID", referencedColumnName = "wahlbezirkID"),
        @JoinColumn(name = "fk_wahlID", referencedColumnName = "wahlID"),
        @JoinColumn(
            name = "fk_waehlerverzeichnisNummer",
            referencedColumnName = "waehlerverzeichnisNummer")
      })
  @NotNull @Size(min = 1) @ToString.Include
  private Set<EingenommenerWahlschein> eingenommeneWahlscheine = new LinkedHashSet<>();

  public void addVermerk(Vermerk vermerk) {
    vermerke.add(vermerk);
    vermerk.setStimmabgabevermerke(this);
  }
}
