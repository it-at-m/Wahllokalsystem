package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import java.time.LocalDate;
import java.util.List;

public interface WahlenClient {

    /**
     * @param wahltag The Request Wahltag - Reference for requested Wahlen
     * @param wahltagNummer The Number of the Wahltag, it could exist more then one Wahltag at same day
     *            if more elections on the day
     * @return List<WahltagModel>
     * @throws WlsException
     *             {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException} if
     *             return would be null
     *             {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException}
     *             if there were trouble during communication
     */
    List<WahlModel> getWahlen(final LocalDate wahltag, final String wahltagNummer) throws WlsException;
}
