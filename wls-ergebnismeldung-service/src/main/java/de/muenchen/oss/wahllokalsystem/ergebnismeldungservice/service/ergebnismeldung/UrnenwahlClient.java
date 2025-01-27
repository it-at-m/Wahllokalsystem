package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

public interface UrnenwahlClient {

    /**
     * @param wahlbezirkID
     * @return
     */
    boolean isGeschlossen(String wahlbezirkID);
}
