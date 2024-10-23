package de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;

public interface WahllokalZustandClient {

    /**
     * @param wahllokalzustandModel references a specific state of the voting room for a wahlId,
     *            wahlbezirkID and time. The Object must contain at least one of the following
     *            information:
     *            {@link WahllokalZustandModel#zuletztGesehen()} containing the information about the
     *            last time the election room was online;
     *            {@link WahllokalZustandModel#letzteAbmeldung()}
     *            containing the information about the last time the room has logged out.
     *            {@link DruckzustandModel#schnellmeldungSendenUhrzeit()},
     *            containing the information about the time at which the document "Schnellmeldung" was
     *            sent;
     *            {@link DruckzustandModel#schnellmeldungDruckUhrzeit()},
     *            containing the information about the time at which the document "Schnellmeldung" was
     *            printed;
     *            {@link DruckzustandModel#niederschriftSendenUhrzeit()},
     *            containing the information about the time at which the document "Niederschrift" was
     *            sent;
     *            {@link DruckzustandModel#niederschriftDruckUhrzeit()},
     *            containing the information about the time at which the document "Niederschrift" was
     *            printed.
     *
     * @throws WlsException
     *             {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException}
     *             if there were trouble during communication
     */
    void postWahllokalZustand(final WahllokalZustandModel wahllokalzustandModel) throws WlsException;
}
