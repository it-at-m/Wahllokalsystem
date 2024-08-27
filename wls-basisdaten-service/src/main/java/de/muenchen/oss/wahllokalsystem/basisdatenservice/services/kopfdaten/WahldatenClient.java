package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import java.time.LocalDate;

public interface WahldatenClient {

    /**
     * @param forDate the date of the election
     * @param withNummer the number of the "Wahltermin" same with number of the "Wahltag"
     * @return BasisdatenDTO
     * @throws WlsException
     *             {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException} if
     *             return would be null
     *             {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException}
     *             if there were trouble during communication
     */
    BasisdatenModel loadBasisdaten(LocalDate forDate, String withNummer) throws WlsException;
}
