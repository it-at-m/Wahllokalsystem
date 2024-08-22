package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import java.time.LocalDate;
import java.util.Set;

public interface WahlbezirkeClient {

    /**
     * @param forDate the date of the election
     * @param withNummer the number of the "Wahltag" same with number of the "Wahltermin"
     * @return Set<WahlbezirkModel>
     * @throws WlsException
     *             {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException} if
     *             return would be null
     *             {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException}
     *             if there were trouble during communication
     */
    Set<WahlbezirkModel> loadWahlbezirke(LocalDate forDate, String withNummer) throws WlsException;
}
