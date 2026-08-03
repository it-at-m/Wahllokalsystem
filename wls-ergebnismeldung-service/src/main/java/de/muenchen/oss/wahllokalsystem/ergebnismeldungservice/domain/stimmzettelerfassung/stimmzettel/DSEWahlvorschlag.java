package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

import static java.sql.Types.VARCHAR;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;
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
public class DSEWahlvorschlag {

  @Id
  @GeneratedValue(generator = "uuid")
  @UuidGenerator
  @JdbcTypeCode(VARCHAR)
  private UUID id;

  @NotNull private String wahlvorschlagID;

  @NotNull private boolean selected;

  @ManyToOne private DSEStimmzettel stimmzettel;

  @OneToMany(mappedBy = "wahlvorschlag")
  private List<DSEKandidat> kandidaten = new LinkedList<>();

  public void addKandidat(final DSEKandidat kandidat) {
    kandidaten.add(kandidat);
    kandidat.setWahlvorschlag(this);
  }
}
