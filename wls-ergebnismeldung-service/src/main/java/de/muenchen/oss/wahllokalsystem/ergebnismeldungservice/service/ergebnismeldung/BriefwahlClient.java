package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

public interface BriefwahlClient {

    /**
     * TODO
     *
     * - sind alle beanstandenten Wahlbriefe die nicht zugelassen sind
     * - Soll als Funktion in den Briefwahlservice landen
     *
     * @param wahlbezirkID
     * @param wahlID
     * @param waehlerverzeichnisNummer
     * @return
     */
    long getAnzahlZurueckgewiesenerWahlbriefe(String wahlbezirkID, String wahlID, long waehlerverzeichnisNummer);
}
