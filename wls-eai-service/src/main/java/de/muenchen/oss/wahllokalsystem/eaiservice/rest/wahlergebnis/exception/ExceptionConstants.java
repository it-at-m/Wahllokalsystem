package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.exception;

public class ExceptionConstants {

    //saveWahlergebnismeldung
    public static final String CODE_SAVEWAHLERGEBNISMELDUNG_WAHLBEZIRKID_FEHLT = "001";
    public static final String MESSAGE_SAVEWAHLERGEBNISMLEDUNG_WAHLBEZIRKID_FEHLT = "Die WahlbezirkID fehlt";
    public static final String CODE_SAVEWAHLERGEBNISMELDUNG_WAHLID_FEHLT = "002";
    public static final String MESSAGE_SAVEWAHLERGEBNISMLEDUNG_WAHLID_FEHLT = "Die WahlID fehlt";

    /**
     * @throws IllegalAccessException when constructor is used
     */
    private ExceptionConstants() throws IllegalAccessException {
        throw new IllegalAccessException("dont create instanced - it is just a holder for constants");
    }
}
