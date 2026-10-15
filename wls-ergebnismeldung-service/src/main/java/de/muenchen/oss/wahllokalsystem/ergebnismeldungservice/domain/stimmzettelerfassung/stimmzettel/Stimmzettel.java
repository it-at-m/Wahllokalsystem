package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
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
public class Stimmzettel {

  @EmbeddedId @ToString.Include @EqualsAndHashCode.Include private StimmzettelID id;

  @NotNull private int invalideVotes;

  @NotNull @Enumerated(EnumType.STRING)
  private StimmzettelGueltigkeit gueltigkeit;

  @OneToMany(mappedBy = "stimmzettel", orphanRemoval = true, cascade = CascadeType.ALL)
  private List<WahlvorstandBeschlussgrund> wahlvorstandBeschlussvorschlag = new LinkedList<>();

  @OneToMany(mappedBy = "stimmzettel", orphanRemoval = true, cascade = CascadeType.ALL)
  private List<SystemBeschlussgrund> systemBeschlussvorschlag = new LinkedList<>();

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "pro", column = @Column(name = "beschluss_pro")),
    @AttributeOverride(name = "contra", column = @Column(name = "beschluss_contra")),
    @AttributeOverride(name = "text", column = @Column(name = "beschluss_text"))
  })
  private Beschlussfassung beschlussfassung;

  @OneToMany(mappedBy = "stimmzettel", orphanRemoval = true, cascade = CascadeType.ALL)
  private List<Wahlvorschlag> wahlvorschlaege = new LinkedList<>();

  public void addWahlvorstandBeschlussvorschlag(
      final WahlvorstandBeschlussgrund wahlvorstandBeschlussgrund) {
    wahlvorstandBeschlussvorschlag.add(wahlvorstandBeschlussgrund);
    wahlvorstandBeschlussgrund.setStimmzettel(this);
  }

  public void addSystemBeschlussvorschlag(final SystemBeschlussgrund systemBeschlussgrund) {
    systemBeschlussvorschlag.add(systemBeschlussgrund);
    systemBeschlussgrund.setStimmzettel(this);
  }

  public void addWahlvorschlag(final Wahlvorschlag wahlvorschlag) {
    wahlvorschlaege.add(wahlvorschlag);
    wahlvorschlag.setStimmzettel(this);
  }
}
