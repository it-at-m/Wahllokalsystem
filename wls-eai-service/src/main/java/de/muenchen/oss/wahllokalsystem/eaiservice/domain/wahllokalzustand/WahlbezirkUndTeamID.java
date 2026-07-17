package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahllokalzustand;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WahlbezirkUndTeamID {
  @NotNull @Size(max = 1000) private String wahlbezirkID;

  @Size(max = 1000) private String teamID;
}
