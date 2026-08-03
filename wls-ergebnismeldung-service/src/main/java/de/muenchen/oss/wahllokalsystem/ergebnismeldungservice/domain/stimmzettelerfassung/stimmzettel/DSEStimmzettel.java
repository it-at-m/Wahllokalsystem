package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.util.LinkedList;
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

  @NotNull private boolean valid;

  @NotNull private int invalideVotes;

  @NotNull
  @Enumerated(EnumType.STRING)
  private StimmzettelGueltigkeit gueltigkeit;

  @OneToMany(mappedBy = "stimmzettel")
  private List<DSEBeschlussvormerkung> beschlussvormerkungen = new LinkedList<>();

  @Embedded
  @AttributeOverrides({
          @AttributeOverride(name="pro", column=@Column(name = "beschluss_pro")),
          @AttributeOverride(name="contra", column=@Column(name = "beschluss_contra")),
          @AttributeOverride(name="text", column=@Column(name = "beschluss_text"))
  })
  private Beschlussfassung beschlussfassung;

  @OneToMany(mappedBy = "stimmzettel")
  private List<DSEWahlvorschlag> wahlvorschlaege = new LinkedList<>();

  public void addBeschlussvormerkung(final DSEBeschlussvormerkung beschlussvormerkung) {
    beschlussvormerkungen.add(beschlussvormerkung);
    beschlussvormerkung.setStimmzettel(this);
  }

  public void addWahlvorschlag(final DSEWahlvorschlag wahlvorschlag) {
    wahlvorschlaege.add(wahlvorschlag);
    wahlvorschlag.setStimmzettel(this);
  }
}
