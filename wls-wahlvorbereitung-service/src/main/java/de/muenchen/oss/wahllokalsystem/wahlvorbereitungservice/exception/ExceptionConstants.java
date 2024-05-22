package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionConstants {

    private static final String CODE_SUCHKRITERIEN_UNVOLLSTAENDIG = "901";
    private static final String MSG_SUCHKRITERIEN_UNVOLLSTAENDIG = "Fehler beim Laden: Suchkriterien unvollständig.";

    private static final String CODE_PARAMS_UNVOLLSTAENDIG = "902";
    private static final String MSG_PARAMS_UNVOLLSTAENDIG = "Fehler beim Speichern: Parameter unvollständig.";

    private static final String CODE_UNSAVEABLE = "903";
    private static final String MSG_UNSAVEABLE = "Fehler beim speichern: Daten konnten nich gespeichert werden.";

    public static ExceptionDataWrapper SUCHKRITERIEN_UNVOLLSTAENDIG = new ExceptionDataWrapper(CODE_SUCHKRITERIEN_UNVOLLSTAENDIG,
            MSG_SUCHKRITERIEN_UNVOLLSTAENDIG);

    public static ExceptionDataWrapper PARAMS_UNVOLLSTAENDIG = new ExceptionDataWrapper(CODE_PARAMS_UNVOLLSTAENDIG, MSG_PARAMS_UNVOLLSTAENDIG);

    public static ExceptionDataWrapper UNSAVEABLE = new ExceptionDataWrapper(CODE_UNSAVEABLE, MSG_UNSAVEABLE);

}
