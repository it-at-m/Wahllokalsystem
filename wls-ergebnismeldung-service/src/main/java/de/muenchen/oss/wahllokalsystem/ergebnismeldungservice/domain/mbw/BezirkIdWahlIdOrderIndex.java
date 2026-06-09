package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BezirkIdWahlIdOrderIndex {

  @NotNull @Size(max = 1000) private String wahlID;

  @NotNull @Size(max = 1000) private String wahlbezirkID;

  private int orderIndex;
}
