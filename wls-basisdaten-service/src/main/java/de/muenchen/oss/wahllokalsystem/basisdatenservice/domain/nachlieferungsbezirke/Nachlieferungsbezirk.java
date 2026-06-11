package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.nachlieferungsbezirke;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common.WahltagIdUndWahlbezirkId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Nachlieferungsbezirk {

  @EmbeddedId @ToString.Include private WahltagIdUndWahlbezirkId wahltagIdUndWahlbezirkId;
}
