package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahllokalzustand;

import static java.sql.Types.VARCHAR;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Druckzustand {

  @JdbcTypeCode(VARCHAR)
  @NotNull private UUID wahlID;

  private LocalDateTime schnellmeldungSendenUhrzeit;

  private LocalDateTime niederschriftSendenUhrzeit;

  private LocalDateTime schnellmeldungDruckUhrzeit;

  private LocalDateTime niederschriftDruckUhrzeit;
}
