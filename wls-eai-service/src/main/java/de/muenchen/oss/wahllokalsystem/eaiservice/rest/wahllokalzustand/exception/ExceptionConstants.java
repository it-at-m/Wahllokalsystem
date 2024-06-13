package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.exception;

public class ExceptionConstants {

    //saveWahllokalZustand
    public static final String CODE_SAVEWAHLLOKALZUSTAND_WAHLBEZIRKID_FEHLT = "001";
    public static final String MESSAGE_SAVEWAHLLOKALZUSTAND_WAHLBEZIRKID_FEHLT = "Die WahlbezirkID fehlt";

    /**
     * @throws IllegalAccessException when constructor is used
     */
    private ExceptionConstants() throws IllegalAccessException {
        throw new IllegalAccessException("dont create instanced - it is just a holder for constants");
    }
}
