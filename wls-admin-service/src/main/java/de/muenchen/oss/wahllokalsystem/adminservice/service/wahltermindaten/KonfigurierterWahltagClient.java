package de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten;

public interface KonfigurierterWahltagClient {

    /**
     * @param konfigurierterWahltag filter for konfigurierten Wahltaa
     *         {@link de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.KonfigurierterWahltagModel}
     * @throws de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException when problem
     *             on retrieving the information occurred
     */
    void postKonfigurierterWahltag(KonfigurierterWahltagModel konfigurierterWahltag);
}
