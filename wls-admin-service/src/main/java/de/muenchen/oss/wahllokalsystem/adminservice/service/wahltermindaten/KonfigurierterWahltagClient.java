package de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten;

import de.muenchen.oss.wahllokalsystem.adminservice.service.common.KonfigurierterWahltagModel;

public interface KonfigurierterWahltagClient {

    /**
     * @param konfigurierterWahltag filter for konfigurierten Wahltaa {@link KonfigurierterWahltagModel}
     * @throws de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException when problem
     *             on retrieving the information occurred
     */
    void postKonfigurierterWahltag(KonfigurierterWahltagModel konfigurierterWahltag);
}
