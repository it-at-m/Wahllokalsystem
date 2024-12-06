package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import java.util.List;

public interface AWerteClient {

    /**
     * @param wahlbezirkID reference to a specific Wahlbezirk
     * @return List<AWerteModel>
     * @throws WlsException
     *             {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException} if
     *             return would be null
     *             {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException}
     *             if there were trouble during communication
     */
    List<AWerteModel> getAWerte(final String wahlbezirkID) throws WlsException;
}
