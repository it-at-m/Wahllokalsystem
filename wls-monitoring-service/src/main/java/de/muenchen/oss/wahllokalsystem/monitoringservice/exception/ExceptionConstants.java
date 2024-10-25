package de.muenchen.oss.wahllokalsystem.monitoringservice.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionConstants {

    //waehleranzahl
    public static final ExceptionDataWrapper GETWAHLBETEILIGUNG_SUCHKRITERIEN_UNVOLLSTAENDIG = new ExceptionDataWrapper("102",
            "Fehler in getWahlbeteiligung(): Parameter unvollstaendig.");
    public static final ExceptionDataWrapper POSTWAHLBETEILIGUNG_UNSAVEABLE = new ExceptionDataWrapper("101",
            "Fehler in postWahlbeteiligung():  Waehleranzahl konnte nicht gespeichert werden.");

    public static ExceptionDataWrapper FAILED_COMMUNICATION_WITH_EAI = new ExceptionDataWrapper("100",
            "Bei der Kommunikation mit dem Aoueai-Service ist ein Fehler aufgetreten. Es konnten daher keine Daten geladen werden.");

}
