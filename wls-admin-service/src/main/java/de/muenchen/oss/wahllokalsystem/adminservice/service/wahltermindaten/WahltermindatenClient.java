package de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;

public interface WahltermindatenClient {

    void putWahltermindaten(final String wahltagID) throws WlsException;

    void deleteWahltermindaten(final String wahltagID) throws WlsException;

}
