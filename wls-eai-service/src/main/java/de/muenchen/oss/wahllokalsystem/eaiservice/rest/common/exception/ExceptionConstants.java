package de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.exception;

public class ExceptionConstants {

    public static final String CODE_FEHLER_BEI_KOMMUNIKATIONS_MIT_FREMDSYSTEM = "901";
    public static final String MESSAGE_FEHLER_BEI_KOMMUNIKATIONS_MIT_FREMDSYSTEM = "Fremdsystem hat Fehler geworfen";
    public static final String MESSAGE_FEHLER_BEI_KOMMUNIKATIONS_MIT_FREMDSYSTEM_MIT_MESSAGE = "Fremdsystem hat Fehler geworfen: %s";

    public static final String CODE_DATENALLGEMEIN_PARAMETER_FEHLEN = "102";
    public static final String MESSAGE_DATENALLGEMEIN_PARAMETER_FEHLEN = "Anfrage ist ungueltig da notwendige Anfragedaten fehlen";

    private static final String CODE_DATENALLGEMEIN_ID_NICHT_KONVERTIERBAR = "103";
    public static final de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper ID_NICHT_KONVERTIERBAR = new de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper(
            CODE_DATENALLGEMEIN_ID_NICHT_KONVERTIERBAR, "");

    /**
     * @throws IllegalAccessException when constructor is used
     */
    private ExceptionConstants() throws IllegalAccessException {
        throw new IllegalAccessException("dont create instanced - it is just a holder for constants");
    }
}
