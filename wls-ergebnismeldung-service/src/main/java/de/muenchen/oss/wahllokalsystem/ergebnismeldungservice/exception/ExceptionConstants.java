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

    public static final ExceptionDataWrapper GET_STATUS_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("606", "getStatus: Parameter unvollstaendig");
    public static final ExceptionDataWrapper POST_STATUS_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("607", "postStatus: Parameter unvollstaendig");
    public static final ExceptionDataWrapper STATUS_UNSAVEABLE = new ExceptionDataWrapper("622", "postStatus: Der Status konnte nicht gespeichtert werden.");

    public static final ExceptionDataWrapper GET_WAHLSCHEINE_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("613", "getWahlscheine: Parameter unvollstaendig");
    public static final ExceptionDataWrapper POST_WAHLSCHEINE_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("613", "postWahlscheine: Parameter unvollstaendig");
    public static final ExceptionDataWrapper WAHLSCHEINE_UNSAVEABLE = new ExceptionDataWrapper("618", "postWahlscheine: Die Wahlscheine konnten nicht gespeichtert werden.");

    public static final ExceptionDataWrapper KOMMUNIKATIONSFEHLER_MIT_MONITORING = new ExceptionDataWrapper("100",
            "Bei der Kommunikation mit dem MonitoringService kam es zu einem Fehler.");
}
