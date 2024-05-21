package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionConstants {

    private static final String CODE_SUCHKRITERIEN_UNVOLLSTAENDIG = "901";
    private static final String MSG_SUCHKRITERIEN_UNVOLLSTAENDIG = "Fehler beim Laden: Suchkriterien unvollständig.";

    public static ExceptionDataWrapper SUCHKRITERIEN_UNVOLLSTAENDIG = new ExceptionDataWrapper(CODE_SUCHKRITERIEN_UNVOLLSTAENDIG,
            MSG_SUCHKRITERIEN_UNVOLLSTAENDIG);

}
