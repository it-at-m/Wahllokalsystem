package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamBezirkUndWahlID implements Serializable {

  @NotNull @Size(max = 1000) private String wahlID;

  @NotNull @Size(max = 1000) private String wahlbezirkID;

  @NotNull @Size(max = 1000) private String teamID;
}
