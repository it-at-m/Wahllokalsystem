package de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import java.util.List;

public interface AWerteClient {

    /**
     * @param wahlbezirkIDs reference to a list of Wahlbezirke
     * @throws WlsException
     *             {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException}
     *             if there were trouble during communication
     */
    void initialiseAWerte(List<String> wahlbezirkIDs);
}
