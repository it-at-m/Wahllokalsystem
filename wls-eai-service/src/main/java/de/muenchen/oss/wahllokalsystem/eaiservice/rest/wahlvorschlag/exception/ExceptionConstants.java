package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper;

public class ExceptionConstants {

    public static final String CODE_WAHLID_FEHLT = "001";
    public static final String MESSAGE_WAHLID_FEHLT = "Die WahlID fehlt";

    public static final String CODE_BEZIRKID_FEHLT = "002";
    public static final String MESSAGE_BEZIRKID_FEHLT = "Die BezirkID fehlt";

    //loadWahlvorschlaege
    public static final ExceptionDataWrapper LOADWAHLVORSCHLAEGE_WAHLID_FEHLT = new ExceptionDataWrapper(
            CODE_WAHLID_FEHLT, MESSAGE_WAHLID_FEHLT);
    public static final ExceptionDataWrapper LOADWAHLVORSCHLAEGE_BEZIRKID_FEHLT = new ExceptionDataWrapper(
            CODE_BEZIRKID_FEHLT, MESSAGE_BEZIRKID_FEHLT);

    //loadWahlvorschlaegeListe
    public static final ExceptionDataWrapper LOADWAHLVORSCHLAEGELISTE_WAHLID_FEHLT = new ExceptionDataWrapper(
            CODE_WAHLID_FEHLT, MESSAGE_WAHLID_FEHLT);

    //loadReferendumvorlagen
    public static final ExceptionDataWrapper LOADREFERENDUMVORLAGEN_WAHLBEZIRKID_FEHLT = new ExceptionDataWrapper(
            CODE_BEZIRKID_FEHLT, MESSAGE_BEZIRKID_FEHLT);
    public static final ExceptionDataWrapper LOADREFERENDUMVORLAGEN_WAHLID_FEHLT = new ExceptionDataWrapper(
            CODE_WAHLID_FEHLT, MESSAGE_WAHLID_FEHLT);

    /**
     * @throws IllegalAccessException when constructor is used
     */
    private ExceptionConstants() throws IllegalAccessException {
        throw new IllegalAccessException("dont create instanced - it is just a holder for constants");
    }
}
