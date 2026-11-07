package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

import static java.sql.Types.VARCHAR;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;

import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.envers.Audited;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Audited
public class Wahlvorschlag {

  @Id
  @GeneratedValue(generator = "uuid")
  @UuidGenerator
  @JdbcTypeCode(VARCHAR)
  private UUID id;

  @NaturalId
  @NotNull private String wahlvorschlagID;

  @NotNull private boolean selected;

  @ManyToOne private Stimmzettel stimmzettel;

  @OneToMany(mappedBy = "wahlvorschlag", orphanRemoval = true, cascade = CascadeType.ALL)
  private Set<Kandidat> kandidaten = new HashSet<>();

  public void addKandidat(final Kandidat kandidat) {
    kandidaten.add(kandidat);
    kandidat.setWahlvorschlag(this);
  }

  @Override
  public int hashCode() {
    return 42;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;

    return Objects.equals(wahlvorschlagID, ((Wahlvorschlag) obj).wahlvorschlagID);
  }
}
