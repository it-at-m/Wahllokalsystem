package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.ErgebnismeldungDTO;

public interface EaiClient {

    /**
     * @param ergebnismeldungDTO data to send
     * @throws de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException when problem
     *             on retrieving the information occurred
     * @throws de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException when no valid
     *             information are given
     */
    void sendErgebnismeldung(ErgebnismeldungDTO ergebnismeldungDTO);
}
