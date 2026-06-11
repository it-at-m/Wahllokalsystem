package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class BedenklicheStimmzettelErfassung {

  @EmbeddedId @ToString.Include private BezirkUndWahlID compositeId;

  @OneToMany(mappedBy = "erfassung", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<BedenklicherStimmzettel> bedenklicheStimmzettel = new ArrayList<>();

  public void addBedenklicheStimmzettel(final BedenklicherStimmzettel bedenklicherStimmzettel) {
    bedenklicheStimmzettel.add(bedenklicherStimmzettel);
    bedenklicherStimmzettel.setErfassung(this);
  }
}
