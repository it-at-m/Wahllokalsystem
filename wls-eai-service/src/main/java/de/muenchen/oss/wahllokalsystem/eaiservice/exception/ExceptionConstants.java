package de.muenchen.oss.wahllokalsystem.eaiservice.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper;

public class ExceptionConstants {

    //loadBasisdaten
    public static final ExceptionDataWrapper LOADBASISDATEN_TAG_FEHLT = new ExceptionDataWrapper("001", "Es ist kein Tag definiert");
    public static final ExceptionDataWrapper LOADBASISDATEN_NUMMER_FEHLT = new ExceptionDataWrapper("001_1", "Es ist keine Wahlnummer angegeben");

    //loadWahlberechtigte
    public static final ExceptionDataWrapper LOADWAHLBERECHTIGTE_SUCHKRITERIEN_UNVOLLSTAENDIG = new ExceptionDataWrapper("001",
            "Wahlberechtigtenkriterien sind nicht vollständig");

    //loadWahltage
    public static final ExceptionDataWrapper LOADWAHLTAGE_TAG_FEHLT = new ExceptionDataWrapper("001", "Es ist kein Tag definiert");

    //loadWahlbezirke
    public static final ExceptionDataWrapper LOADWAHLEBZIRKE_WAHLTAG_FEHLT = new ExceptionDataWrapper("001", "Es ist kein Wahltag angegeben");
    public static final ExceptionDataWrapper LOADWAHLEBZIRKE_NUMMER_FEHLT = new ExceptionDataWrapper("001_1", "Es ist keine Wahltag-Nummer angegeben");

    //loadWahlen
    public static final ExceptionDataWrapper LOADWAHLEN_WAHLTAG_FEHLT = new ExceptionDataWrapper("001", "Es ist kein Wahltag angegeben");
    public static final ExceptionDataWrapper LOADWAHLEN_NUMMER_FEHLT = new ExceptionDataWrapper("001_1", "Es ist keine Wahlnummer angegeben");

    /**
     * @throws IllegalAccessException when constructor is used
     */
    private ExceptionConstants() throws IllegalAccessException {
        throw new IllegalAccessException("dont create instanced - it is just a holder for constants");
    }
}
