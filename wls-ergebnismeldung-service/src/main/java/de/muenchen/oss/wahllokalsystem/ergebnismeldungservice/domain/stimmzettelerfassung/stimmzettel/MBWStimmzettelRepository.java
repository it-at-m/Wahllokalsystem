package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel;

import java.util.List;

public interface MBWStimmzettelRepository {

  List<WahlvorschlagStimmzettelAnzahl> getStapelA(String wahlID, String wahlbezirkID);

  List<WahlvorschlagStimmzettelAnzahl> getStapelBGroupedByWahlvorschlag(
      String wahlID, String wahlbezirkID);

  List<KandidatStimmenAnzahl> getStapelBGroupedByKandidat(String wahlID, String wahlbezirkID);

  long getStapelD(String wahlID, String wahlbezirkID);

  List<KandidatStimmenAnzahl> getStapelC(String wahlID, String wahlbezirkID);
}
