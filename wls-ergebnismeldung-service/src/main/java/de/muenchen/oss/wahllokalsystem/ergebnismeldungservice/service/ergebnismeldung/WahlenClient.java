package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

public interface WahlenClient {

    /**
     * TODO
     *
     * Exception wenn:
     * - die Wahl nicht gibt
     * - es keinen aktiven Wahltag gibt
     *
     * @param wahlID
     * @return
     */
    WahlartModel getWahlartOfCurrentWahltag(final String wahlID);
}
