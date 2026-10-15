package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

import static java.sql.Types.VARCHAR;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class SystemBeschlussgrund {

  @Id
  @GeneratedValue(generator = "uuid")
  @UuidGenerator
  @JdbcTypeCode(VARCHAR)
  private UUID id;

  @NotNull @Enumerated(EnumType.STRING)
  private SystemBeschlussgrundTyp reason;

  @ManyToOne private Stimmzettel stimmzettel;
}
