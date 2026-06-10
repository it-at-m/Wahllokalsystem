package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.nachlieferungsbezirke;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common.WahltagIdUndWahlbezirkId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "nachlieferungsbezirk")
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Nachlieferungsbezirk {

  @EmbeddedId @ToString.Include private WahltagIdUndWahlbezirkId wahltagIdUndWahlbezirkId;
}
