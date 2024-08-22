package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper;

public class ExceptionConstants {

    public static ExceptionDataWrapper GETEREIGNIS_SUCHKRITERIEN_UNVOLLSTAENDIG = new ExceptionDataWrapper("100", "Fehler in getEreignis(): Suchkriterien unvollständig.");   // fachlich
    public static ExceptionDataWrapper POSTEREIGNIS_PARAMS_UNVOLLSTAENDIG = new ExceptionDataWrapper("102", "Fehler in postEreignis(): Parameter unvollständig.");   // fachlich
    public static ExceptionDataWrapper SAVEEREIGNIS_UNSAVABLE = new ExceptionDataWrapper("103", "Fehler in postEreignis(): Ereignis konnte nicht gespeichert werden.");   // technisch
}
