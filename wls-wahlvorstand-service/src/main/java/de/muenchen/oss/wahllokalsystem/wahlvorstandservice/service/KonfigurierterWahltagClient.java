package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;

public interface KonfigurierterWahltagClient {

    /**
     * @return KonfigurierterWahltagDTO
     * @throws WlsException
     *             {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException} if
     *             return would be null
     *             {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException}
     *             if there were trouble during communication
     */
    KonfigurierterWahltagModel getKonfigurierterWahltag() throws WlsException;
}
