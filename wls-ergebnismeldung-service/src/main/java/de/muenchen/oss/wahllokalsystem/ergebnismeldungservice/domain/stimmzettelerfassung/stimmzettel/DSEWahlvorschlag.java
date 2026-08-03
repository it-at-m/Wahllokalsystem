package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class DSEWahlvorschlag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull private String wahlvorschlagID;

  @NotNull private boolean selected;

  @ManyToOne private DSEStimmzettel stimmzettel;

  @OneToMany(mappedBy = "wahlvorschlag")
  private List<DSEKandidat> kandidaten = new LinkedList<>();

  public void addKandidat(final DSEKandidat kandidat) {
    kandidaten.add(kandidat);
    kandidat.setWahlvorschlag(this);
  }
}
