package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BedenklicheStimmzettel {

  @EmbeddedId private BezirkUndWahlID compositeId;

    @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "BedenklicherStimmzettel",
      joinColumns = {
        @JoinColumn(name = "fk_wahlid", referencedColumnName = "wahlid"),
        @JoinColumn(name = "fk_wahlbezirkid", referencedColumnName = "wahlbezirkid"),
      })
  private List<BedenklicherStimmzettel> bedenklicheStimmzettel;
}
