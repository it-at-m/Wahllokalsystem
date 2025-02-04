package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

public interface BriefwahlClient {

    long getAnzahlZurueckgewiesenerWahlbriefe(String wahlbezirkID, String wahlID, long waehlerverzeichnisNummer);
}
