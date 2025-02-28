package de.muenchen.oss.wahllokalsystem.adminservice.service.common;

import java.util.List;

public interface WahlbezirkeClient {

    /**
     * @param wahltagID filter for Wahltaa
     * @return list of Wahlbezirke
     *         {@link WahlbezirkModel}
     * @throws de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException when problem
     *             on retrieving the information occurred
     */
    List<WahlbezirkModel> getWahlbezirke(String wahltagID);
}
