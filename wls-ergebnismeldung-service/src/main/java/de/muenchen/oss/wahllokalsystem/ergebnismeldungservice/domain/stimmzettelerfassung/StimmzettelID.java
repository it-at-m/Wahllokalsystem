package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

@Data
public class StimmzettelID implements Serializable {
  @NotNull private String wahlbezirkID;

  @NotNull private String wahlID;

  @NotNull private String teamID;

  private int stimmzettelkennung;
}
