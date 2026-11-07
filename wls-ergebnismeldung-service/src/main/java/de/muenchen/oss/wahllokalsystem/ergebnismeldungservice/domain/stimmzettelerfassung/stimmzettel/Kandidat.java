package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

import static java.sql.Types.VARCHAR;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.UUID;
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
public class Kandidat {

  @Id
  @GeneratedValue(generator = "uuid")
  @UuidGenerator
  @JdbcTypeCode(VARCHAR)
  private UUID id;

  @NaturalId
  @NotNull private KandidatId kandidatID;

  @ManyToOne private Wahlvorschlag wahlvorschlag;

  @NotNull private boolean discarded;

  private Integer votesByVoter;

  private Integer invalidVotes;

  private Integer votesByWahlvorschlag;

  @Override
  public int hashCode() {
    return 42;
  }

  @Override
  public boolean equals(Object obj) {
    return Objects.equals(kandidatID, ((Kandidat) obj).kandidatID);
  }
}
