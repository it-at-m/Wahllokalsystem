package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

public interface UrnenwahlClient {

    /**
     * Check if any for wahlbezirk is closed
     *
     * @param wahlbezirkID wahlbezirk to check
     * @return true when any wahl is closed
     * @throws de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException wenn
     */
    boolean isGeschlossen(String wahlbezirkID);
}
