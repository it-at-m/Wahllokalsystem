package de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten;

import java.util.List;

public interface WahlbezirkeClient {

    /**
     * @param wahltagID filter for Wahltaa
     * @return list of Wahlbezirke
     *         {@link de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.WahlbezirkModel}
     * @throws de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException when problem
     *             on retrieving the information occurred
     */
    List<WahlbezirkModel> getWahlbezirke(String wahltagID);
}
