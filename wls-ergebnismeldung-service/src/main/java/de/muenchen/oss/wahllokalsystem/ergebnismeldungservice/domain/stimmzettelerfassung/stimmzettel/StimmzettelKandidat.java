package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class StimmzettelKandidat {
  @NotNull private String kandidatId;

  private boolean isDiscarded;

  private int votesByVoter;
}
