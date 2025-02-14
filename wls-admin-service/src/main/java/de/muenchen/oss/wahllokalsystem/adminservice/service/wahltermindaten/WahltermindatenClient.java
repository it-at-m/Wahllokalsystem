package de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten;

public interface WahltermindatenClient {

    /**
     * @param wahltagID filter for Wahltaa
     * @throws de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException when problem
     *             on retrieving the information occurred
     */
    void putWahltermindaten(final String wahltagID);

    /**
     * @param wahltagID filter for Wahltaa
     * @throws de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException when problem
     *             on retrieving the information occurred
     */
    void deleteWahltermindaten(final String wahltagID);
}
