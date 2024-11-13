package de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;

public interface WahllokalZustandClient {

    /**
     *
     * @param wahlbezirkID the id of the election room;
     * @param zuletztGesehen containing the information about the last time the election room was
     *            online;
     * @throws WlsException {@link TechnischeWlsException} if there were trouble during communication
     */
    void postLastSeen(final String wahlbezirkID, final LocalDateTime zuletztGesehen) throws WlsException;

    /**
     *
     * @param wahlbezirkID the id of the election room;
     * @param letzteAbmeldung containing the information about the last time the room has logged out.
     * @throws WlsException {@link TechnischeWlsException} if there were trouble during communication
     */
    void postLetzteAbmeldung(final String wahlbezirkID, final LocalDateTime letzteAbmeldung) throws WlsException;

    /**
     *
     * @param bezirkUndWahlID composed identification of election ID and election room
     * @param schnellmeldungSendungsuhrzeit time this Data was created, the Schnellmeldung was sent
     * @throws WlsException {@link TechnischeWlsException} if there were trouble during communication
     */
    void postSchnellmeldungSendungsuhrzeit(final BezirkUndWahlID bezirkUndWahlID, final LocalDateTime schnellmeldungSendungsuhrzeit) throws WlsException;

    /**
     *
     * @param bezirkUndWahlID composed identification of election and election room
     * @param schnellmeldungDruckuhrzeit the time this Data was created, the Schnellmeldung was printed
     * @throws WlsException {@link TechnischeWlsException} if there were trouble during communication
     */
    void postSchnellmeldungDruckuhrzeit(final BezirkUndWahlID bezirkUndWahlID, final LocalDateTime schnellmeldungDruckuhrzeit) throws WlsException;

    /**
     *
     * @param bezirkUndWahlID composed identification of election ID and election room
     * @param niederschriftSendungsuhrzeit time this Data was created, the Niederschrift was sent
     * @throws WlsException {@link TechnischeWlsException} if there were trouble during communication
     */
    void postNiederschriftSendungsuhrzeit(final BezirkUndWahlID bezirkUndWahlID, final LocalDateTime niederschriftSendungsuhrzeit) throws WlsException;

    /**
     *
     * @param bezirkUndWahlID composed identification of election and election room
     * @param niederschriftDruckuhrzeit the time this Data was created, the Niederschrift was printed
     * @throws WlsException {@link TechnischeWlsException} if there were trouble during communication
     */
    void postNiederschriftDruckuhrzeit(final BezirkUndWahlID bezirkUndWahlID, final LocalDateTime niederschriftDruckuhrzeit) throws WlsException;
}
