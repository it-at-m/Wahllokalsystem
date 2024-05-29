package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.exception;

public class ExceptionConstants {

    //loadWahlvorschlaege
    String CODE_LOADWAHLVORSCHLAEGE_WAHLID_FEHLT = "001";
    String MESSAGE_LOADWAHLVORSCHLAEGE_WAHLID_FEHLT = "Die WahlID fehlt";

    //loadWahlvorschlaege
    String CODE_LOADWAHLVORSCHLAEGE_BEZIRKID_FEHLT = "002";
    String MESSAGE_LOADWAHLVORSCHLAEGE_BEZIRKID_FEHLT = "Die BezirkID fehlt";

    //loadWahlvorschlaegeListe
    String CODE_LOADWAHLVORSCHLAEGELISTE_WAHLID_FEHLT = "001";
    String MESSAGE_LOADWAHLVORSCHLAEGELISTE_WAHLID_FEHLT = "Die WahlID fehlt";

    //loadReferendumvorlagen
    String CODE_LOADREFERENDUMVORLAGEN_WAHLBEZIRKID_FEHLT = "001";
    String MESSAGE_LOADREFERENDUMVORLAGEN_WAHLBEZIRKID_FEHLT = "Die WahlbezirkID fehlt";
    String CODE_LOADREFERENDUMVORLAGEN_WAHLID_FEHLT = "002";
    String MESSAGE_LOADREFERENDUMVORLAGEN_WAHLID_FEHLT = "Die WahlID fehlt";

    /**
     * @throws IllegalAccessException when constructor is used
     */
    private ExceptionConstants() throws IllegalAccessException {
        throw new IllegalAccessException("dont create instanced - it is just a holder for constants");
    }
}
