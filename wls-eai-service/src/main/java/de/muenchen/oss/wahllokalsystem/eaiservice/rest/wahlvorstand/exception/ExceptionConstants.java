package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.exception;

public class ExceptionConstants {
    //loadWahlvorstand
    String CODE_LOADWAHLVORSTAND_SUCHKRITERIEN_UNVOLLSTAENDIG = "001";
    String MESSAGE_LOADWAHLVORSTAND_SUCHKRITERIEN_UNVOLLSTAENDIG = "Wahlvorstandskriterien sind nicht vollständig";
    //saveAnwesenheit
    String CODE_SAVEANWESENHEIT_IDENTIFIKATOR_FEHLT = "002";
    String MESSAGE_SAVEANWESENHEIT_IDENTIFIKATOR_FEHLT = "Mindestens ein Identifikator für eine Aktualisierung fehlt";

    String CODE_SAVEANWESENHEIT_BEZIRKID_FEHLT = "003";
    String MESSAGE_SAVEANWESENHEIT_BEZIRKID_FEHLT = "Bezirk fehlt";

    String CODE_SAVEANWESENHEIT_ANWESENHEITBEGINN_FEHLT = "004";
    String MESSAGE_SAVEANWESENHEIT_ANWESENHEITBEGINN_FEHLT = "Es ist keine Information hinterlegt seit wann diese Anwesenheit gilt";

    String CODE_DATENALLGEMEIN_WAHLVORSTANDSMITGLIEDFUNKTION_UNGUELTIGE_FUNKTION = "101";
    String MESSAGE_DATENALLGEMEIN_WAHLVORSTANDSMITGLIEDFUNKTION_UNGUELTIGE_FUNKTION = "Die definierte Funktion des Wahlvorstandsmitgliedes ist nicht gueltig";

    /**
     * @throws IllegalAccessException when constructor is used
     */
    private ExceptionConstants() throws IllegalAccessException {
        throw new IllegalAccessException("dont create instanced - it is just a holder for constants");
    }
}
