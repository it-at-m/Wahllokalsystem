package de.muenchen.oss.wahllokalsystem.adminservice.service.konfigurierterwahltag;

public interface WahlenClient {

    /**
     * @throws de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException when problem
     *             on retrieving the information occurred
     */
    void resetWahlen();
}
