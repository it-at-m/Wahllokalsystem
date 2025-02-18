package de.muenchen.oss.wahllokalsystem.adminservice.service.wahlen;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import java.util.List;

public interface WahlenClient {

    /**
     * @param wahltagID reference to a specific wahltag
     * @return List of <WahlModel>
     * @throws WlsException
     *             {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException} if
     *             return would be null
     *             {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException}
     *             if there were trouble during communication
     */
    List<WahlModel> getWahlen(final String wahltagID) throws WlsException;

    /**
     * @param wahltagID reference to a specific wahltag
     * @param wahlen List of <WahlModel> to be saved
     * @throws WlsException
     *             {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException} if
     *             return would be null
     *             {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException}
     *             if there were trouble during communication
     */
    void postWahlen(String wahltagID, List<WahlModel> wahlen);
}
