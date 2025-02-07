package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

public interface UrnenwahlClient {

    /**
     * Check if the wahlbezirk is closed
     *
     * @param wahlbezirkID wahlbezirk to check
     * @return true when then wahlbezirk is closed
     * @throws de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException when problem
     *             on retrieving the information occurred
     * @throws de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException when no
     *             wahlbezirk with the given ID exists
     */
    boolean isWahlbezirkGeschlossen(String wahlbezirkID);
}
