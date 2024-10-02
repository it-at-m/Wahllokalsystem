package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahltagWithNummer;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;

public interface WahldatenClient {

    /**
     * @param wahltagWithNummer filter for requested Basisdaten
     * @return BasisdatenDTO
     * @throws WlsException
     *             {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException} if
     *             return would be null
     *             {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException}
     *             if there were trouble during communication
     */
    BasisdatenModel loadBasisdaten(WahltagWithNummer wahltagWithNummer) throws WlsException;
}
