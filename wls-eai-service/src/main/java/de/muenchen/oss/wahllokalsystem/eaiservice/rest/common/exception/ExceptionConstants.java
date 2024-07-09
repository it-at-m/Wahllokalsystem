package de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper;

public class ExceptionConstants {

    public static final String CODE_FEHLER_BEI_KOMMUNIKATIONS_MIT_FREMDSYSTEM = "901";
    public static final String MESSAGE_FEHLER_BEI_KOMMUNIKATIONS_MIT_FREMDSYSTEM = "Fremdsystem hat Fehler geworfen";
    public static final String MESSAGE_FEHLER_BEI_KOMMUNIKATIONS_MIT_FREMDSYSTEM_MIT_MESSAGE = "Fremdsystem hat Fehler geworfen: %s";

    private static final String CODE_DATENALLGEMEIN_PARAMETER_FEHLEN = "102";
    private static final String MESSAGE_DATENALLGEMEIN_PARAMETER_FEHLEN = "Anfrage ist ungueltig da notwendige Anfragedaten fehlen";
    public static final ExceptionDataWrapper DATENALLGEMEIN_PARAMETER_FEHLEN = new ExceptionDataWrapper(CODE_DATENALLGEMEIN_PARAMETER_FEHLEN,
            MESSAGE_DATENALLGEMEIN_PARAMETER_FEHLEN);

    private static final String CODE_DATENALLGEMEIN_ID_NICHT_KONVERTIERBAR = "103";
    public static final ExceptionDataWrapper ID_NICHT_KONVERTIERBAR = new ExceptionDataWrapper(
            CODE_DATENALLGEMEIN_ID_NICHT_KONVERTIERBAR, "ID nicht konvertierbar");

    /**
     * @throws IllegalAccessException when constructor is used
     */
    private ExceptionConstants() throws IllegalAccessException {
        throw new IllegalAccessException("dont create instanced - it is just a holder for constants");
    }
}
