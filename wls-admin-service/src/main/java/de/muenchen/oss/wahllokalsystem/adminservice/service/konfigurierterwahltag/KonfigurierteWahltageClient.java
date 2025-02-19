package de.muenchen.oss.wahllokalsystem.adminservice.service.konfigurierterwahltag;

import de.muenchen.oss.wahllokalsystem.adminservice.service.common.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import java.util.List;

public interface KonfigurierteWahltageClient {

    /**
     * @return list of KonfigurierteWahltage
     *         {@link de.muenchen.oss.wahllokalsystem.adminservice.service.common.KonfigurierterWahltagModel}
     * @throws WlsException
     *             {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException} if
     *             return would be null
     *             {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException}
     *             if there were trouble during communication
     */
    List<KonfigurierterWahltagModel> getKonfigurierteWahltage() throws WlsException;

}
