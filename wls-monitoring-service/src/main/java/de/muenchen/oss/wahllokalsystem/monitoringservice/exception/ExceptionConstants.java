package de.muenchen.oss.wahllokalsystem.monitoringservice.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionConstants {

    //waehleranzahl
    public static final ExceptionDataWrapper GETWAHLBETEILIGUNG_SUCHKRITERIEN_UNVOLLSTAENDIG = new ExceptionDataWrapper("102",
            "Fehler in getWahlbeteiligung(): Parameter unvollstaendig.");
    public static ExceptionDataWrapper GETWAHLBETEILIGUNG_KEINE_DATEN = new ExceptionDataWrapper("302", "Die Wahlbeteiligung konnte nicht geladen werden.");
    public static final ExceptionDataWrapper POSTWAHLBETEILIGUNG_UNSAVEABLE = new ExceptionDataWrapper("101",
            "Fehler in postWahlbeteiligung():  Waehleranzahl konnte nicht gespeichert werden.");

    public static final ExceptionDataWrapper POST_LASTSEEN_SUCHKRITERIEN_UNVOLLSTAENDIG = new ExceptionDataWrapper("10§",
            "Fehler in postLastSeen():  Parameter unvollstaendig.");
    public static final ExceptionDataWrapper POST_LETZTEABMELDUNG_SUCHKRITERIEN_UNVOLLSTAENDIG = new ExceptionDataWrapper("10$",
            "Fehler in postLetzteAbmeldung():  Parameter unvollstaendig.");
    public static final ExceptionDataWrapper POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG = new ExceptionDataWrapper("108",
            "Fehler in postSchnellmeldungSendungsuhrzeit():  Parameter unvollstaendig.");
    public static final ExceptionDataWrapper POST_SCHNELLMELDUNG_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG = new ExceptionDataWrapper("106",
            "Fehler in postSchnellmeldungDruckuhrzeit():  Parameter unvollstaendig.");
    public static final ExceptionDataWrapper POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG = new ExceptionDataWrapper("107",
            "Fehler in postNiederschriftSendungsuhrzeit():  Parameter unvollstaendig.");
    public static final ExceptionDataWrapper POST_NIEDERSCHRIFT_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG = new ExceptionDataWrapper("105",
            "Fehler in postNiederschriftDruckuhrzeit():  Parameter unvollstaendig.");
    public static final ExceptionDataWrapper DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG = new ExceptionDataWrapper("108",
            "Nicht zurodnerbarer Fehler in Wahllokalzustand-Service:  Parameter unvollstaendig.");

    public static ExceptionDataWrapper FAILED_COMMUNICATION_WITH_EAI = new ExceptionDataWrapper("100",
            "Bei der Kommunikation mit dem Aoueai-Service ist ein Fehler aufgetreten. Es konnten daher keine Daten geladen werden.");

}
