package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper;

public class ExceptionConstants {
    //loadWahlvorstand
    private static final String CODE_LOADWAHLVORSTAND_SUCHKRITERIEN_UNVOLLSTAENDIG = "001";
    private static final String MESSAGE_LOADWAHLVORSTAND_SUCHKRITERIEN_UNVOLLSTAENDIG = "Wahlvorstandskriterien sind nicht vollständig";
    public static final ExceptionDataWrapper LOADWAHLVORSTAND_SUCHKRITERIEN_UNVOLLSTAENDIG = new ExceptionDataWrapper(
        CODE_LOADWAHLVORSTAND_SUCHKRITERIEN_UNVOLLSTAENDIG, MESSAGE_LOADWAHLVORSTAND_SUCHKRITERIEN_UNVOLLSTAENDIG);

    //saveAnwesenheit
    private static final String CODE_SAVEANWESENHEIT_IDENTIFIKATOR_FEHLT = "002";
    private static final String MESSAGE_SAVEANWESENHEIT_IDENTIFIKATOR_FEHLT = "Mindestens ein Identifikator für eine Aktualisierung fehlt";
    public static final ExceptionDataWrapper SAVEANWESENHEIT_IDENTIFIKATOR_FEHLT = new ExceptionDataWrapper(CODE_SAVEANWESENHEIT_IDENTIFIKATOR_FEHLT,
        MESSAGE_SAVEANWESENHEIT_IDENTIFIKATOR_FEHLT);

    private static final String CODE_SAVEANWESENHEIT_BEZIRKID_FEHLT = "003";
    private static final String MESSAGE_SAVEANWESENHEIT_BEZIRKID_FEHLT = "Bezirk fehlt";
    public static final ExceptionDataWrapper SAVEANWESENHEIT_BEZIRKID_FEHLT = new ExceptionDataWrapper(CODE_SAVEANWESENHEIT_BEZIRKID_FEHLT,
        MESSAGE_SAVEANWESENHEIT_BEZIRKID_FEHLT);

    private static final String CODE_SAVEANWESENHEIT_ANWESENHEITBEGINN_FEHLT = "004";
    private static final String MESSAGE_SAVEANWESENHEIT_ANWESENHEITBEGINN_FEHLT = "Es ist keine Information hinterlegt seit wann diese Anwesenheit gilt";
    public static final ExceptionDataWrapper SAVEANWESENHEIT_ANWESENHEITBEGINN_FEHLT = new ExceptionDataWrapper(CODE_SAVEANWESENHEIT_ANWESENHEITBEGINN_FEHLT,
        MESSAGE_SAVEANWESENHEIT_ANWESENHEITBEGINN_FEHLT);

    private static final String CODE_DATENALLGEMEIN_WAHLVORSTANDSMITGLIEDFUNKTION_UNGUELTIGE_FUNKTION = "101";
    private static final String MESSAGE_DATENALLGEMEIN_WAHLVORSTANDSMITGLIEDFUNKTION_UNGUELTIGE_FUNKTION = "Die definierte Funktion des Wahlvorstandsmitgliedes ist nicht gueltig";
    public static final ExceptionDataWrapper DATENALLGEMEIN_WAHLVORSTANDSMITGLIEDFUNKTION_UNGUELTIGE_FUNKTION = new ExceptionDataWrapper(
        CODE_DATENALLGEMEIN_WAHLVORSTANDSMITGLIEDFUNKTION_UNGUELTIGE_FUNKTION, MESSAGE_DATENALLGEMEIN_WAHLVORSTANDSMITGLIEDFUNKTION_UNGUELTIGE_FUNKTION);

    // loadWahlvorschlaege
    private static final String CODE_LOADWAHLVORSCHLAEGE_SUCHKRITERIEN_UNVOLLSTAENDIG = "005";
    private static final String MESSAGE_LOADWAHLVORSCHLAEGE_SUCHKRITERIEN_UNVOLLSTAENDIG = "Wahlvorschlaegekriterien sind nicht vollständig";
    public static final ExceptionDataWrapper LOADWAHLVORSCHLAEGE_SUCHKRITERIEN_UNVOLLSTAENDIG = new ExceptionDataWrapper(
        CODE_LOADWAHLVORSCHLAEGE_SUCHKRITERIEN_UNVOLLSTAENDIG, MESSAGE_LOADWAHLVORSCHLAEGE_SUCHKRITERIEN_UNVOLLSTAENDIG);

    /**
     * @throws IllegalAccessException when constructor is used
     */
    private ExceptionConstants() throws IllegalAccessException {
        throw new IllegalAccessException("dont create instanced - it is just a holder for constants");
    }
}
