package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Kandidat {

  @EmbeddedId private KandidatId id;

  @ManyToOne private Wahlvorschlag wahlvorschlag;

  @NotNull private boolean discarded;

  private Integer votesByVoter;

  private Integer invalidVotes;

  private Integer votesByWahlvorschlag;
}
