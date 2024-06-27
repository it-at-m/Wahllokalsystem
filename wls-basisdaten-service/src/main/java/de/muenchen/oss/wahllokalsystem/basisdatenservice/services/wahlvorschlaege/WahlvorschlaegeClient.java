package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;

public interface WahlvorschlaegeClient {

    /**
     * @param bezirkUndWahlID Reference for requestes Wahlvorschlaege
     * @return Model with Wahlvorschlaegen
     * @throws WlsException
     *             {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException} if
     *             return would be null
     *             {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException}
     *             if there were trouble during communication
     */
    WahlvorschlaegeModel getWahlvorschlaege(BezirkUndWahlID bezirkUndWahlID) throws WlsException;

}
