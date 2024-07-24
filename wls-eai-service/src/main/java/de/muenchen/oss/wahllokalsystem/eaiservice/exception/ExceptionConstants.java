package de.muenchen.oss.wahllokalsystem.eaiservice.exception;

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

    //loadBasisdaten
    public static final ExceptionDataWrapper LOADBASISDATEN_TAG_FEHLT = new ExceptionDataWrapper("001", "Es ist kein Tag definiert");
    public static final ExceptionDataWrapper LOADBASISDATEN_NUMMER_FEHLT = new ExceptionDataWrapper("001_1", "Es ist keine Wahlnummer angegeben");

    //loadWahlberechtigte
    public static final ExceptionDataWrapper LOADWAHLBERECHTIGTE_SUCHKRITERIEN_UNVOLLSTAENDIG = new ExceptionDataWrapper("001",
        "Wahlberechtigtenkriterien sind nicht vollständig");

    //loadWahltage
    public static final ExceptionDataWrapper LOADWAHLTAGE_TAG_FEHLT = new ExceptionDataWrapper("001", "Es ist kein Tag definiert");

    //loadWahlbezirke
    public static final ExceptionDataWrapper LOADWAHLBEZIRKE_WAHLTAG_FEHLT = new ExceptionDataWrapper("001", "Es ist kein Wahltag angegeben");
    public static final ExceptionDataWrapper LOADWAHLBEZIRKE_NUMMER_FEHLT = new ExceptionDataWrapper("001_1", "Es ist keine Wahltag-Nummer angegeben");

    //loadWahlen
    public static final ExceptionDataWrapper LOADWAHLEN_WAHLTAG_FEHLT = new ExceptionDataWrapper("001", "Es ist kein Wahltag angegeben");
    public static final ExceptionDataWrapper LOADWAHLEN_NUMMER_FEHLT = new ExceptionDataWrapper("001_1", "Es ist keine Wahlnummer angegeben");

    //saveWahlbeteiligung
    public static final ExceptionDataWrapper SAVEWAHLBETEILIGUNG_WAHLBEZIRKID_FEHLT = new ExceptionDataWrapper(CODE_BEZIRKID_FEHLT, MESSAGE_BEZIRKID_FEHLT);
    public static final ExceptionDataWrapper SAVEWAHLBETEILIGUNG_WAHLID_FEHLT = new ExceptionDataWrapper(CODE_WAHLID_FEHLT, MESSAGE_WAHLID_FEHLT);
    public static final ExceptionDataWrapper SAVEWAHLBETEILIGUNG_MELDEZEITPUNKT_FEHLT = new ExceptionDataWrapper("003", "Der Meldezeitpunkt fehlt");

    /**
     * @throws IllegalAccessException when constructor is used
     */
    private ExceptionConstants() throws IllegalAccessException {
        throw new IllegalAccessException("dont create instanced - it is just a holder for constants");
    }
}
