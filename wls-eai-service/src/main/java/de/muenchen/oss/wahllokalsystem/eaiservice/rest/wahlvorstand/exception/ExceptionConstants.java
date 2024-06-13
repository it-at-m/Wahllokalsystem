package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.exception;

public class ExceptionConstants {
    //loadWahlvorstand
    public static final String CODE_LOADWAHLVORSTAND_SUCHKRITERIEN_UNVOLLSTAENDIG = "001";
    public static final String MESSAGE_LOADWAHLVORSTAND_SUCHKRITERIEN_UNVOLLSTAENDIG = "Wahlvorstandskriterien sind nicht vollständig";
    //saveAnwesenheit
    public static final String CODE_SAVEANWESENHEIT_IDENTIFIKATOR_FEHLT = "002";
    public static final String MESSAGE_SAVEANWESENHEIT_IDENTIFIKATOR_FEHLT = "Mindestens ein Identifikator für eine Aktualisierung fehlt";

    public static final String CODE_SAVEANWESENHEIT_BEZIRKID_FEHLT = "003";
    public static final String MESSAGE_SAVEANWESENHEIT_BEZIRKID_FEHLT = "Bezirk fehlt";

    public static final String CODE_SAVEANWESENHEIT_ANWESENHEITBEGINN_FEHLT = "004";
    public static final String MESSAGE_SAVEANWESENHEIT_ANWESENHEITBEGINN_FEHLT = "Es ist keine Information hinterlegt seit wann diese Anwesenheit gilt";

    public static final String CODE_DATENALLGEMEIN_WAHLVORSTANDSMITGLIEDFUNKTION_UNGUELTIGE_FUNKTION = "101";
    public static final String MESSAGE_DATENALLGEMEIN_WAHLVORSTANDSMITGLIEDFUNKTION_UNGUELTIGE_FUNKTION = "Die definierte Funktion des Wahlvorstandsmitgliedes ist nicht gueltig";

    /**
     * @throws IllegalAccessException when constructor is used
     */
    private ExceptionConstants() throws IllegalAccessException {
        throw new IllegalAccessException("dont create instanced - it is just a holder for constants");
    }
}
