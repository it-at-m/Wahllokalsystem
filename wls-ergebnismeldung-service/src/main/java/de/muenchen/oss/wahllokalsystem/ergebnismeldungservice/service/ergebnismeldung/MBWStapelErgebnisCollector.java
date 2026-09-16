package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

public interface MBWStapelErgebnisCollector {
  MBWErgebnisseModel getErgebnisse(final String wahlID, final String wahlbezirkID);
}
