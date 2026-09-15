package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;

public interface ErgebnismeldungsErgebnisseMapper {

  ErgebnismeldungsErgebnisseModel getErgebnismeldungErgebnisse(
      String wahlID, String wahlbezirkID, WahlartModel wahlart, MeldungsartModel meldungsart);

  boolean canHandleWahlart(WahlartModel wahlart);
}
