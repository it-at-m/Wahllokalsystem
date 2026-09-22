package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

import java.util.List;
import org.springframework.data.repository.query.Param;

public interface MBWStimmzettelRepository {

  List<WahlvorschlagStimmzettelAnzahl> getStapelA(
      @Param("wahlID") String wahlID, @Param("wahlbezirkID") String wahlbezirkID);

  List<WahlvorschlagStimmzettelAnzahl> getStapelB(
      @Param("wahlID") String wahlID, @Param("wahlbezirkID") String wahlbezirkID);

  long getStapelD(@Param("wahlID") String wahlID, @Param("wahlbezirkID") String wahlbezirkID);

  List<KandidatStimmenAnzahl> getStapelBC(
      @Param("wahlID") String wahlID, @Param("wahlbezirkID") String wahlbezirkID);
}
