package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

public interface CurrentWahltagClient {

    /**
     * @return ID of the current active wahltag
     * @throws de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException when problem
     *             on retrieving the information occurred
     * @throws de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException when no
     *             wahltag is active
     */
    String getWahltagID();
}
