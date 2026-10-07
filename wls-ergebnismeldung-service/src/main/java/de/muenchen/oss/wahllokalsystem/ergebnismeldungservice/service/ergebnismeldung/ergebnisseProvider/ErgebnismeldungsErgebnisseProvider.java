package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.ergebnisseProvider;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlartModel;

public interface ErgebnismeldungsErgebnisseProvider {

  ErgebnismeldungsErgebnisseModel getErgebnismeldungErgebnisse(
      String wahlID, String wahlbezirkID, WahlartModel wahlart, MeldungsartModel meldungsart);

  boolean canHandleWahlart(WahlartModel wahlart);
}
