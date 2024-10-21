package de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;

public interface WaehleranzahlClient {

    /**
     * @param waehleranzahlModel references a specific number of voters for a wahlId, wahlbezirkID and
     *            time
     * @throws WlsException
     *             {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException}
     *             if there were trouble during communication
     */
    void postWahlbeteiligung(final WaehleranzahlModel waehleranzahlModel) throws WlsException;
}
