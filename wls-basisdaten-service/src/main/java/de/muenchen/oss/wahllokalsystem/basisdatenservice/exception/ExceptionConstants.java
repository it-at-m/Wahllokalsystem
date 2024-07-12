package de.muenchen.oss.wahllokalsystem.basisdatenservice.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionKonstanten;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionConstants {

    private static final String CODE_SUCHKRITERIEN_UNVOLLSTAENDIG = "310";
    private static final String MSG_SUCHKRITERIEN_UNVOLLSTAENDIG = "getWahlvorschlaege: Suchkriterien unvollständig.";

    private static final String CODE_UNSAVEABLE = "903";
    private static final String MSG_UNSAVEABLE = "Fehler beim speichern: Daten konnten nicht gespeichert werden.";

    public static ExceptionDataWrapper SUCHKRITERIEN_UNVOLLSTAENDIG = new ExceptionDataWrapper(CODE_SUCHKRITERIEN_UNVOLLSTAENDIG,
            MSG_SUCHKRITERIEN_UNVOLLSTAENDIG);

    public static ExceptionDataWrapper UNSAVEABLE = new ExceptionDataWrapper(CODE_UNSAVEABLE, MSG_UNSAVEABLE);

    public static ExceptionDataWrapper NULL_FROM_CLIENT = new ExceptionDataWrapper(ExceptionKonstanten.CODE_ENTITY_NOT_FOUND, "not found");

    public static ExceptionDataWrapper FAILED_COMMUNICATION_WITH_EAI = new ExceptionDataWrapper("100",
            "Bei der Kommunikation mit dem Aoueai-Service ist ein Fehler aufgetreten. Es konnten daher keine Daten geladen werden.");

    public static ExceptionDataWrapper GETHANDBUCH_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("301", "getHandbuch: Suchkriterien unvollständig.");
    public static ExceptionDataWrapper GETHANDBUCH_KEINE_DATEN = new ExceptionDataWrapper("302", "Das Handbuch konnte nicht geladen werden.");

    public static ExceptionDataWrapper POSTHANDBUCH_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("315", "postHandbuch: Suchkriterien unvollständig.");
    public static ExceptionDataWrapper POSTHANDBUCH_SPEICHERN_NICHT_ERFOLGREICH = new ExceptionDataWrapper("316",
            "postHandbuch: Das speichern des Handbuches war nicht erfolgreich.");

}
