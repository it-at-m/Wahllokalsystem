package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionConstants {

    public static final ExceptionDataWrapper KOMMUNIKATIONSFEHLER_MIT_INFOMANAGEMENT = new ExceptionDataWrapper("200",
            "Bei der Kommunikation mit dem InfomanagementService kam es zu einem Fehler.");
    public static final ExceptionDataWrapper INFOMANAGEMENT_WAHLTAG_NULL_OR_EMPTY = new ExceptionDataWrapper("201",
            "Der konfigurierteWahltag aus dem InfomanagementService ist null oder leer.");

    public static final ExceptionDataWrapper KOMMUNIKATIONSFEHLER_MIT_BASISDATEN = new ExceptionDataWrapper("300",
            "Bei der Kommunikation mit dem BasisdatenService kam es zu einem Fehler.");
    public static final ExceptionDataWrapper BASISDATEN_WAHLEN_EMPTY = new ExceptionDataWrapper("301",
            "Die Wahlen aus dem BasisdatenService sind leer.");
    public static final ExceptionDataWrapper BASISDATEN_WAHL_NOT_FOUND = new ExceptionDataWrapper("302",
            "Die Wahl zur übergebenen wahlID wurde nicht gefunden.");

    public static final ExceptionDataWrapper KOMMUNIKATIONSFEHLER_MIT_AOUEAI = new ExceptionDataWrapper("400",
            "Bei der Kommunikation mit dem AoueaiService kam es zu einem Fehler.");

    public static final ExceptionDataWrapper KOMMUNIKATIONSFEHLER_MIT_WAHLVORBEREITUNG = new ExceptionDataWrapper("500",
            "Bei der Kommunikation mit dem WahlvorbereitungsService kam es zu einem Fehler.");
    public static final ExceptionDataWrapper WAHLVORBEREITUNG_SCHLIESSUNGSUHRZEIT_NULL_OR_EMPTY = new ExceptionDataWrapper("501",
            "Die Schliessungsuhrzeit aus dem WahlvorbereitungsService ist leer.");

    public static final ExceptionDataWrapper KOMMUNIKATIONSFEHLER_MIT_BRIEFWAHL = new ExceptionDataWrapper("510",
            "Bei der Kommunikation mit dem BriefwahlService kam es zu einem Fehler.");
    public static final ExceptionDataWrapper BRIEFWAHL_BEANSTANDETEWAHLBRIEFE_NULL_OR_EMPTY = new ExceptionDataWrapper("511",
            "Die beanstandeten Wahlbriefe aus dem BriefwahlService sind leer.");

    public static ExceptionDataWrapper GETAWERTE_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("600",
            "getAWerte: Parameter unvollstaendig");
    public static ExceptionDataWrapper GETAWERTE_UNSAVEABLE = new ExceptionDataWrapper("601",
            "getAWerte: Die AWerte vom Client konnten nicht gespeichert werden.");

    public static final ExceptionDataWrapper GET_STATUS_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("606",
            "getStatus: Parameter unvollstaendig");
    public static final ExceptionDataWrapper POST_STATUS_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("607",
            "postStatus: Parameter unvollstaendig");
    public static final ExceptionDataWrapper STATUS_UNSAVEABLE = new ExceptionDataWrapper("622",
            "postStatus: Der Status konnte nicht gespeichert werden.");

    public static final ExceptionDataWrapper GET_WAHLSCHEINE_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("612",
            "getWahlscheine: Parameter unvollstaendig");
    public static final ExceptionDataWrapper POST_WAHLSCHEINE_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("613",
            "postWahlscheine: Parameter unvollstaendig");
    public static final ExceptionDataWrapper WAHLSCHEINE_UNSAVEABLE = new ExceptionDataWrapper("618",
            "postWahlscheine: Die Wahlscheine konnten nicht gespeichert werden.");

    public static final ExceptionDataWrapper GET_STIMMZETTELUMSCHLAEGE_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("610",
            "getStimmzettelumschlaege: Parameter unvollstaendig");
    public static final ExceptionDataWrapper POST_STIMMZETTELUMSCHLAEGE_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("611",
            "postStimmzettelumschlaege: Parameter unvollstaendig");
    public static final ExceptionDataWrapper STIMMZETTELUMSCHLAEGE_UNSAVEABLE = new ExceptionDataWrapper("619",
            "postStimmzettelumschlaege: Die Stimmzettelumschlaege konnten nicht gespeichert werden.");
    public static final ExceptionDataWrapper WAHLBEZIRKART_NOT_LOADABLE = new ExceptionDataWrapper("800",
            "Die Wahlbezirkart des Principals konnte nicht geladen werden.");

    public static final ExceptionDataWrapper KOMMUNIKATIONSFEHLER_MIT_MONITORING = new ExceptionDataWrapper("100",
            "Bei der Kommunikation mit dem MonitoringService kam es zu einem Fehler.");

    public static final ExceptionDataWrapper GET_BEGRUENDUNG_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("101",
            "getBegruendung: Parameter unvollstaendig.");
    public static final ExceptionDataWrapper POST_BEGRUENDUNG_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("103",
            "postBegruendung: Parameter unvollstaendig.");
    public static final ExceptionDataWrapper BEGRUENDUNG_UNSAVEABLE = new ExceptionDataWrapper("624",
            "postBegruendung: Die Begruendung konnte nicht gespeichert werden.");

    public static final ExceptionDataWrapper GET_STIMMABGABEVERMERKE_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("608",
            "getStimmabgabevermerke: Parameter unvollstaendig");
    public static final ExceptionDataWrapper POST_STIMMABGABEVERMERKE_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("609",
            "postStimmabgabevermerke: Parameter unvollstaendig");
    public static final ExceptionDataWrapper STIMMABGABEVERMERKE_UNSAVEABLE = new ExceptionDataWrapper("620",
            "postStimmabgabevermerke: Die Stimmabgabevermerke konnten nicht gespeichert werden.");

    public static final ExceptionDataWrapper POST_AUSDRUCK_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("628",
            "postAusdruck: Parameter unvollstaendig");
    public static final ExceptionDataWrapper GET_AUSDRUCK_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("629",
            "getAusdruck: Parameter unvollstaendig");

    public static final ExceptionDataWrapper GET_ERGEBNISSE_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("614",
            "getErgebnisse: Parameter unvollstaendig.");
    public static final ExceptionDataWrapper POST_ERGEBNISSE_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("615",
            "postErgebnisse: Parameter unvollstaendig.");
    public static final ExceptionDataWrapper ERGEBNISSE_UNSAVEABLE = new ExceptionDataWrapper("621",
            "postErgebnisse: Die Ergebnisse konnten nicht gespeichert werden.");

    public static final ExceptionDataWrapper SENDERGEBNISSE_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("617",
            "sendErgebnisse: Parameter unvollstaendig");
    public static final ExceptionDataWrapper FORCEERGEBNISSE_WRONG_USAGE = new ExceptionDataWrapper("627",
            "forceErgebnisse: forceErgebnisse sollte nie in Benutzung sein wenn beide Validierungsstatus auf NICHT_GESENDET stehen.");

    public static final ExceptionDataWrapper WAHL_NICHT_GESCHLOSSEN = new ExceptionDataWrapper("625",
            "Die Wahl zu dieser WahlID in diesem Wahlbezirk ist noch nicht geschlossen.");

    public static final ExceptionDataWrapper WAHLART_NOT_IMPLEMENTED = new ExceptionDataWrapper("631", "getErgebnisCsv: Wahlart nicht implementiert");
    public static final ExceptionDataWrapper SENDERGEBNISSE_STAPELN_UNVOLLSTAENDIG = new ExceptionDataWrapper("632", "sendErgebnisse: Stapeln unvollstaendig");
    public static final ExceptionDataWrapper SENDERGEBNISSE_STIMMABGABEVERMERKE_UNVOLLSTAENDIG = new ExceptionDataWrapper("633",
            "sendErgebnisse: Stimmabgabevermerke unvollstaendig");
    public static final ExceptionDataWrapper SENDERGEBNISSE_WAHLSCHEINE_UNVOLLSTAENDIG = new ExceptionDataWrapper("634",
            "sendErgebnisse: Wahlscheine unvollstaendig");
    public static final ExceptionDataWrapper SENDERGEBNISSE_AWERTE_UNVOLLSTAENDIG = new ExceptionDataWrapper("635", "sendErgebnisse: A-Werte unvollstaendig");

    public static final ExceptionDataWrapper MAPPING_AOUEAI = new ExceptionDataWrapper("900",
            "Beim Mapping einer Klasse aus der Aoueai waren notwendige Felder null oder leer.");
    public static final ExceptionDataWrapper MAPPING_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("901",
            "Beim Mapping einer Klasse aus der Aoueai waren WLS-Paramter mit null befuellt."); //T
}
