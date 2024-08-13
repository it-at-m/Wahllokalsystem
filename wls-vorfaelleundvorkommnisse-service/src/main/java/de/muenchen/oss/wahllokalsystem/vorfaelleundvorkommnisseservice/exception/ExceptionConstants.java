package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper;

public class ExceptionConstants {

   /* // todo: hab ich das richtig umgewandelt?
    static final int INDEX_CATEGORY = 0;
    static final int INDEX_MESSAGE = 1;

    public static final String CODE_GET_EREIGNISSE_SUCHKRITERIEN_UNVOLLSTAENDIG = "100";
    public static final String CODE_POST_EREIGNISSE_PARAMS_UNVOLLSTAENDIG = "102";
    public static final String CODE_SAVE_EREIGNISSE_UNSAVABLE = "103";

    private static Map<String, String[]> map = new HashMap<>();
    static {
        map.put(CODE_GET_EREIGNISSE_SUCHKRITERIEN_UNVOLLSTAENDIG, new String[] {"F", "Fehler in getEreignis(): Suchkriterien unvollständig."});
        map.put(CODE_POST_EREIGNISSE_PARAMS_UNVOLLSTAENDIG, new String[] {"F", "Fehler in postEreignis(): Parameter unvollständig."});
        map.put(CODE_SAVE_EREIGNISSE_UNSAVABLE, new String[] {"T", "Fehler in postEreignis(): Ereignis konnte nicht gespeichert werden."});
    }

    public static String[] get(String code) {
        return map.get(code);
    }*/

    public static ExceptionDataWrapper GETEREIGNIS_SUCHKRITERIEN_UNVOLLSTAENDIG = new ExceptionDataWrapper("100", "Fehler in getEreignis(): Suchkriterien unvollständig.");   // fachlich
    public static ExceptionDataWrapper POSTEREIGNIS_PARAMS_UNVOLLSTAENDIG = new ExceptionDataWrapper("102", "Fehler in postEreignis(): Parameter unvollständig.");   // fachlich
    public static ExceptionDataWrapper SAVEEREIGNIS_UNSAVABLE = new ExceptionDataWrapper("103", "Fehler in postEreignis(): Ereignis konnte nicht gespeichert werden.");   // technisch
}
