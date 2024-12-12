package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.sender;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;

public interface StatusClient {

    /**
     * @param bezirkUndWahlID composed identification of election ID and election room
     * @param schnellmeldungSendungsuhrzeit time this Data was created, the Schnellmeldung was sent
     * @throws WlsException {@link TechnischeWlsException} if there were trouble during communication
     */
    void postSchnellmeldungSendungsuhrzeit(final BezirkUndWahlID bezirkUndWahlID, final LocalDateTime schnellmeldungSendungsuhrzeit) throws WlsException;

    /**
     * @param bezirkUndWahlID composed identification of election and election room
     * @param schnellmeldungDruckuhrzeit the time this Data was created, the Schnellmeldung was printed
     * @throws WlsException {@link TechnischeWlsException} if there were trouble during communication
     */
    void postSchnellmeldungDruckuhrzeit(final BezirkUndWahlID bezirkUndWahlID, final LocalDateTime schnellmeldungDruckuhrzeit) throws WlsException;

    /**
     * @param bezirkUndWahlID composed identification of election ID and election room
     * @param niederschriftSendungsuhrzeit time this Data was created, the Niederschrift was sent
     * @throws WlsException {@link TechnischeWlsException} if there were trouble during communication
     */
    void postNiederschriftSendungsuhrzeit(final BezirkUndWahlID bezirkUndWahlID, final LocalDateTime niederschriftSendungsuhrzeit) throws WlsException;

    /**
     * @param bezirkUndWahlID composed identification of election and election room
     * @param niederschriftDruckuhrzeit the time this Data was created, the Niederschrift was printed
     * @throws WlsException {@link TechnischeWlsException} if there were trouble during communication
     */
    void postNiederschriftDruckuhrzeit(final BezirkUndWahlID bezirkUndWahlID, final LocalDateTime niederschriftDruckuhrzeit) throws WlsException;
}
