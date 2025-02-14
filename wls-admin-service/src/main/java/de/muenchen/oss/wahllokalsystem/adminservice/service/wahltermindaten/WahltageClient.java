package de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten;

import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahltagModel;
import java.util.List;

public interface WahltageClient {

    /**
     * @return list of Wahltage
     *         {@link WahltagModel}
     * @throws de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException when problem
     *             on retrieving the information occurred
     */
    List<WahltagModel> getWahltage();
}
