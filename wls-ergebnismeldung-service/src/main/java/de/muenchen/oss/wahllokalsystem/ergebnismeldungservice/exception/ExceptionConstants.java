package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionConstants {

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
            "getBegruendung: Parameter unvollständig.");
    public static final ExceptionDataWrapper POST_BEGRUENDUNG_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("103",
            "postBegruendung: Parameter unvollständig.");
    public static final ExceptionDataWrapper BEGRUENDUNG_UNSAVEABLE = new ExceptionDataWrapper("624",
            "postStatus: Der Status konnte nicht gespeichert werden.");

    public static final ExceptionDataWrapper POST_AUSDRUCK_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("628",
            "postAusdruck: Parameter unvollstaendig");
    public static final ExceptionDataWrapper GET_AUSDRUCK_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("629",
            "getAusdruck: Parameter unvollstaendig");
}
