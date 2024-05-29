package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.exception;

public class ExceptionConstants {

    //loadBasisdaten
    public static final String CODE_LOADBASISDATEN_TAG_FEHLT = "001";
    public static final String MESSAGE_LOADBASISDATEN_TAG_FEHLT = "Es ist kein Tag definiert";

    //loadWahlberechtigte
    public static final String CODE_LOADWAHLBERECHTIGTE_SUCHKRITERIEN_UNVOLLSTAENDIG = "001";
    public static final String MESSAGE_LOADWAHLBERECHTIGTE_SUCHKRITERIEN_UNVOLLSTAENDIG = "Wahlberechtigtenkriterien sind nicht vollständig";

    //loadWahltage
    public static final String CODE_LOADWAHLTAGE_TAG_FEHLT = "001";
    public static final String MESSAGE_LOADWAHLTAGE_TAG_FEHLT = "Es ist kein Tag definiert";

    //loadWahlbezirke
    public static final String CODE_LOADWAHLBEZIRKE_WAHLTAG_FEHLT = "001";
    public static final String CODE_LOADWAHLBEZIRKE_NUMMER_FEHLT = "001_1";
    public static final String MESSAGE_LOADWAHLEBZIRKE_WAHLTAG_FEHLT = "Es ist kein Wahltag angegeben";
    public static final String MESSAGE_LOADWAHLEBZIRKE_NUMMER_FEHLT = "Es ist keine Wahltag-Nummer angegeben";

    //loadWahlen
    public static final String CODE_LOADWAHLEN_WAHLTAG_FEHLT = "001";
    public static final String MESSAGE_LOADWAHLEN_WAHLTAG_FEHLT = "Es ist kein Wahltag angegeben";
    public static final String CODE_LOADWAHLEN_NUMMER_FEHLT = "001_1";
    public static final String MESSAGE_LOADWAHLEN_NUMMER_FEHLT = "Es ist keine Wahlnummer angegeben";

    /**
     * @throws IllegalAccessException when constructor is used
     */
    private ExceptionConstants() throws IllegalAccessException {
        throw new IllegalAccessException("dont create instanced - it is just a holder for constants");
    }
}
