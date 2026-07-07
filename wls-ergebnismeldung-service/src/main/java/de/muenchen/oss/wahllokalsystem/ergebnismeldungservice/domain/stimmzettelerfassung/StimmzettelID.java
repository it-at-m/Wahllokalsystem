package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StimmzettelID {
  @NotNull private String wahlbezirkID;

  @NotNull private String wahlID;

  @NotNull private String teamID;

  private int stimmzettelkennung;
}
