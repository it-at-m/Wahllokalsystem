package de.muenchen.oss.wahllokalsystem.basisdatenservice.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionKonstanten;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionConstants {

    private static final String CODE_GETWAHLVORSCHLAEGE_JACKSON_PARSE_ERROR = "311";
    private static final String MSG_GETWAHLVORSCHLAEGE_JACKSON_PARSE_ERROR = "Beim parsen der Wahlvorschlaege für die Datenbank ist ein Fehler aufgetreten.";

    private static final String CODE_SUCHKRITERIEN_UNVOLLSTAENDIG = "901";
    private static final String MSG_SUCHKRITERIEN_UNVOLLSTAENDIG = "Fehler beim Laden: Suchkriterien unvollständig.";

    private static final String CODE_PARAMS_UNVOLLSTAENDIG = "902";
    private static final String MSG_PARAMS_UNVOLLSTAENDIG = "Fehler beim Speichern: Parameter unvollständig.";

    private static final String CODE_UNSAVEABLE = "903";
    private static final String MSG_UNSAVEABLE = "Fehler beim speichern: Daten konnten nicht gespeichert werden.";

    public static ExceptionDataWrapper PARSE_ERROR = new ExceptionDataWrapper(CODE_GETWAHLVORSCHLAEGE_JACKSON_PARSE_ERROR,
            MSG_GETWAHLVORSCHLAEGE_JACKSON_PARSE_ERROR);

    public static ExceptionDataWrapper SUCHKRITERIEN_UNVOLLSTAENDIG = new ExceptionDataWrapper(CODE_SUCHKRITERIEN_UNVOLLSTAENDIG,
            MSG_SUCHKRITERIEN_UNVOLLSTAENDIG);

    public static ExceptionDataWrapper PARAMS_UNVOLLSTAENDIG = new ExceptionDataWrapper(CODE_PARAMS_UNVOLLSTAENDIG, MSG_PARAMS_UNVOLLSTAENDIG);

    public static ExceptionDataWrapper UNSAVEABLE = new ExceptionDataWrapper(CODE_UNSAVEABLE, MSG_UNSAVEABLE);

    public static ExceptionDataWrapper NULL_FROM_CLIENT = new ExceptionDataWrapper(ExceptionKonstanten.CODE_ENTITY_NOT_FOUND, "not found");

    public static ExceptionDataWrapper FAILED_COMMUNICATION_WITH_EAI = new ExceptionDataWrapper("100",
            "Bei der Kommunikation mit dem Aoueai-Service ist ein Fehler aufgetreten. Es konnten daher keine Daten geladen werden.");

}
