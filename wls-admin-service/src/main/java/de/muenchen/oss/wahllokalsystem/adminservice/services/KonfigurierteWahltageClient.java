package de.muenchen.oss.wahllokalsystem.adminservice.services;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;

public interface KonfigurierteWahltageClient {

    /**
     * @return KonfigurierterWahltagDTO
     * @throws WlsException {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException} if return would be null
     *                      {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException} if there were trouble during communication
     */
    KonfigurierterWahltagModel getKonfigurierterWahltag() throws WlsException;

}
