package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.WahltagWithNummer;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import java.util.List;

public interface WahlenClient {

    /**
     * @param wahltagWithNummer reference to a specific event on a date of election
     * @return List<WahltagModel>
     * @throws WlsException
     *             {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException} if
     *             return would be null
     *             {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException}
     *             if there were trouble during communication
     */
    List<WahlModel> getWahlen(final WahltagWithNummer wahltagWithNummer) throws WlsException;
}
