package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper;

public class ExceptionConstants {

    //loadWahlvorschlaege
    public static final String CODE_LOADWAHLVORSCHLAEGE_WAHLID_FEHLT = "001";
    public static final String MESSAGE_LOADWAHLVORSCHLAEGE_WAHLID_FEHLT = "Die WahlID fehlt";
    public static final String CODE_LOADWAHLVORSCHLAEGE_BEZIRKID_FEHLT = "002";
    public static final String MESSAGE_LOADWAHLVORSCHLAEGE_BEZIRKID_FEHLT = "Die BezirkID fehlt";
    private static final String CODE_LOADWAHLVORSCHLAEGE_SUCHKRITERIEN_UNVOLLSTAENDIG = "005";
    private static final String MESSAGE_LOADWAHLVORSCHLAEGE_SUCHKRITERIEN_UNVOLLSTAENDIG = "Wahlvorschlaegekriterien sind nicht vollstaendig";
    public static final ExceptionDataWrapper LOADWAHLVORSCHLAEGE_SUCHKRITERIEN_UNVOLLSTAENDIG = new ExceptionDataWrapper(
        CODE_LOADWAHLVORSCHLAEGE_SUCHKRITERIEN_UNVOLLSTAENDIG, MESSAGE_LOADWAHLVORSCHLAEGE_SUCHKRITERIEN_UNVOLLSTAENDIG);

    //loadWahlvorschlaegeListe
    public static final String CODE_LOADWAHLVORSCHLAEGELISTE_WAHLID_FEHLT = "001";
    public static final String MESSAGE_LOADWAHLVORSCHLAEGELISTE_WAHLID_FEHLT = "Die WahlID fehlt";

    //loadReferendumvorlagen
    public static final String CODE_LOADREFERENDUMVORLAGEN_WAHLBEZIRKID_FEHLT = "001";
    public static final String MESSAGE_LOADREFERENDUMVORLAGEN_WAHLBEZIRKID_FEHLT = "Die WahlbezirkID fehlt";
    public static final String CODE_LOADREFERENDUMVORLAGEN_WAHLID_FEHLT = "002";
    public static final String MESSAGE_LOADREFERENDUMVORLAGEN_WAHLID_FEHLT = "Die WahlID fehlt";

    /**
     * @throws IllegalAccessException when constructor is used
     */
    private ExceptionConstants() throws IllegalAccessException {
        throw new IllegalAccessException("dont create instanced - it is just a holder for constants");
    }
}
