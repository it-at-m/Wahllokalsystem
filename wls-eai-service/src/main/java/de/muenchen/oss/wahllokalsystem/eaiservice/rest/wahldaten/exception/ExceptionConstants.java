package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.exception;

public class ExceptionConstants {

    //loadBasisdaten
    String CODE_LOADBASISDATEN_TAG_FEHLT = "001";
    String MESSAGE_LOADBASISDATEN_TAG_FEHLT = "Es ist kein Tag definiert";

    //loadWahlberechtigte
    String CODE_LOADWAHLBERECHTIGTE_SUCHKRITERIEN_UNVOLLSTAENDIG = "001";
    String MESSAGE_LOADWAHLBERECHTIGTE_SUCHKRITERIEN_UNVOLLSTAENDIG = "Wahlberechtigtenkriterien sind nicht vollständig";

    //loadWahltage
    String CODE_LOADWAHLTAGE_TAG_FEHLT = "001";
    String MESSAGE_LOADWAHLTAGE_TAG_FEHLT = "Es ist kein Tag definiert";

    //loadWahlbezirke
    String CODE_LOADWAHLBEZIRKE_WAHLTAG_FEHLT = "001";
    String CODE_LOADWAHLBEZIRKE_NUMMER_FEHLT = "001_1";
    String MESSAGE_LOADWAHLEBZIRKE_WAHLTAG_FEHLT = "Es ist kein Wahltag angegeben";
    String MESSAGE_LOADWAHLEBZIRKE_NUMMER_FEHLT = "Es ist keine Wahltag-Nummer angegeben";

    //loadWahlen
    String CODE_LOADWAHLEN_WAHLTAG_FEHLT = "001";
    String MESSAGE_LOADWAHLEN_WAHLTAG_FEHLT = "Es ist kein Wahltag angegeben";
    String CODE_LOADWAHLEN_NUMMER_FEHLT = "001_1";
    String MESSAGE_LOADWAHLEN_NUMMER_FEHLT = "Es ist keine Wahlnummer angegeben";

    /**
     * @throws IllegalAccessException when constructor is used
     */
    private ExceptionConstants() throws IllegalAccessException {
        throw new IllegalAccessException("dont create instanced - it is just a holder for constants");
    }
}
