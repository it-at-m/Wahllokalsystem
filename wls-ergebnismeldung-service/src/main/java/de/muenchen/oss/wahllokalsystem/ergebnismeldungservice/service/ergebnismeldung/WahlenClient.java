package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

public interface WahlenClient {

    /**
     * @param wahltagID id of election day on that the election is taking place
     * @param wahlID The unique identifier of the election on the current election day
     * @return The election type model containing type information
     * @throws de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException when problem
     *             on retrieving the information occurred
     * @throws de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException when current
     *             election day is unknown or no election with wahlID
     *             exists for the election day
     */
    WahlartModel getWahlart(final String wahltagID, final String wahlID);
}
