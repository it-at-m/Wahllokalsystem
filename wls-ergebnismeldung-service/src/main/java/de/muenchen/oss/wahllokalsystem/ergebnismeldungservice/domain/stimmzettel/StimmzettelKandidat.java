package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettel;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class StimmzettelKandidat {
  private String kandidatId;
  private boolean isDiscarded;
  private int votesByVoter;
}
