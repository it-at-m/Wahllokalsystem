package de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.exception;

public class ExceptionConstants {

    public static final String CODE_DATENALLGEMEIN_ID_NICHT_KONVERTIERBAR = "103";

    public static final String CODE_FEHLER_BEI_KOMMUNIKATIONS_MIT_FREMDSYSTEM = "901";
    public static final String MESSAGE_FEHLER_BEI_KOMMUNIKATIONS_MIT_FREMDSYSTEM = "Fremdsystem hat Fehler geworfen";
    public static final String MESSAGE_FEHLER_BEI_KOMMUNIKATIONS_MIT_FREMDSYSTEM_MIT_MESSAGE = "Fremdsystem hat Fehler geworfen: %s";

    public static final String CODE_DATENALLGEMEIN_PARAMETER_FEHLEN = "102";
    public static final String MESSAGE_DATENALLGEMEIN_PARAMETER_FEHLEN = "Anfrage ist ungueltig da notwendige Anfragedaten fehlen";

    /**
     * @throws IllegalAccessException when constructor is used
     */
    private ExceptionConstants() throws IllegalAccessException {
        throw new IllegalAccessException("dont create instanced - it is just a holder for constants");
    }
}
