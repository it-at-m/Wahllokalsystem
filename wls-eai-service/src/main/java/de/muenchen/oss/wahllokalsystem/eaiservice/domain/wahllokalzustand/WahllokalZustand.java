package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahllokalzustand;

import static java.sql.Types.VARCHAR;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.BaseEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WahllokalZustand extends BaseEntity {

  @JdbcTypeCode(VARCHAR)
  private UUID wahlbezirkID;

  private String teamID;

  private LocalDateTime zuletztGesehen;

  private LocalDateTime letzteAbmeldung;

  @ElementCollection
  @CollectionTable(
      name = "Druckzustand",
      joinColumns = {
        @JoinColumn(name = "wahllokalzustandID"),
      })
  private Set<Druckzustand> druckzustaende;
}
