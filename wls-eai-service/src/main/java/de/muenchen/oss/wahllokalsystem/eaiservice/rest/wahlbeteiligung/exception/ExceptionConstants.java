package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlbeteiligung.exception;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.exception.ExceptionDataWrapper;

public class ExceptionConstants {

    public static final String CODE_SAVEWAHLBETEILIGUNG_WAHLBEZIRKID_FEHLT = "001";
    public static final String MESSAGE_SAVEWAHLBETEILIGUNG_WAHLBEZIRKID_FEHLT = "Die WahlbezirkID fehlt";
    public static final ExceptionDataWrapper SAVEWAHLBETEILIGUNG_WAHLBEZIRKID_FEHLT = new ExceptionDataWrapper(CODE_SAVEWAHLBETEILIGUNG_WAHLBEZIRKID_FEHLT,
            MESSAGE_SAVEWAHLBETEILIGUNG_WAHLBEZIRKID_FEHLT);

    public static final String CODE_SAVEWAHLBETEILIGUNG_WAHLID_FEHLT = "002";
    public static final String MESSAGE_SAVEWAHLBETEILIGUNG_WAHLID_FEHLT = "Die WahlID fehlt";
    public static final ExceptionDataWrapper SAVEWAHLBETEILIGUNG_WAHLID_FEHLT = new ExceptionDataWrapper(CODE_SAVEWAHLBETEILIGUNG_WAHLID_FEHLT,
            MESSAGE_SAVEWAHLBETEILIGUNG_WAHLID_FEHLT);

    public static final String CODE_SAVEWAHLBETEILIGUNG_MELDEZEITPUNKT_FEHLT = "003";
    public static final String MESSAGE_SAVEWAHLBETEILIGUNG_MELDEZEITPUNKT_FEHLT = "Der Meldezeitpunkt fehlt";
    public static final ExceptionDataWrapper SAVEWAHLBETEILIGUNG_MELDEZEITPUNKT_FEHLT = new ExceptionDataWrapper(CODE_SAVEWAHLBETEILIGUNG_MELDEZEITPUNKT_FEHLT,
            MESSAGE_SAVEWAHLBETEILIGUNG_MELDEZEITPUNKT_FEHLT);

    /**
     * @throws IllegalAccessException when constructor is used
     */
    private ExceptionConstants() throws IllegalAccessException {
        throw new IllegalAccessException("dont create instanced - it is just a holder for constants");
    }
}
