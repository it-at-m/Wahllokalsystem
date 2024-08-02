package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import java.time.LocalDate;

public interface WahldatenClient {

    /**
     * @return BasisdatenDTO
     * @throws WlsException
     *             {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException} if
     *             return would be null
     *             {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException}
     *             if there were trouble during communication
     */
    BasisdatenModel loadBasisdaten(LocalDate forDate, String withNummer) throws WlsException;
}
