package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class KandidatId implements Serializable {

  @NotNull private String kandidatID;

  @NotNull private int nennungsNummer;

  @Override
  public int hashCode() {
    return 42;
  }

  @Override
  public boolean equals(Object obj) {
    if  (this == obj) return true;

    return Objects.equals(kandidatID, ((KandidatId) obj).kandidatID) && Objects.equals(nennungsNummer, ((KandidatId) obj).nennungsNummer);
  }
}
