package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper;

public class ExceptionConstants {
    public static final ExceptionDataWrapper KOMMUNIKATIONSFEHLER_MIT_AOUEAI = new ExceptionDataWrapper("100",
            "Bei der Kommunikation mit dem Aoueai-Service ist ein Fehler aufgetreten."); // technisch (todo kommentar löschen)
    public static final ExceptionDataWrapper AOUEAI_WAHLVORSTAND_NULL = new ExceptionDataWrapper("101",
            "Der Wahlvorstand des Aoueai-Service ist null.");   // fachlich (todo kommentar löschen)
    public static final ExceptionDataWrapper KOMMUNIKATIONSFEHLER_MIT_BASISDATEN = new ExceptionDataWrapper("200",
            "Bei der Kommunikation mit dem Basisdaten-Service ist ein Fehler aufgetreten."); // technisch (todo kommentar löschen)
    public static final ExceptionDataWrapper BASISDATEN_ANTWORT_NULL = new ExceptionDataWrapper("201",
            "Die Antwort des Basisdaten-Service ist null.");    // fachlich (todo kommentar löschen)
    public static final ExceptionDataWrapper KOMMUNIKATIONSFEHLER_MIT_INFOMANAGEMENT = new ExceptionDataWrapper("300",
            "Bei der Kommunikation mit dem Infomanagement-Service ist ein Fehler aufgetreten."); // technisch (todo kommentar löschen)
    public static final ExceptionDataWrapper INFOMANAGEMENT_WAHLTAG_NULL_OR_EMPTY = new ExceptionDataWrapper("301",
            "Der aktive Wahltag des Infomanagement-Service ist null oder leer.");    // fachlich (todo kommentar löschen)
    public static final ExceptionDataWrapper GETWAHLVORSTAND_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("400",
            "getWahlvorstand: Suchkriterien unvollständig.");   // fachlich (todo kommentar löschen)
    public static final ExceptionDataWrapper GETWAHLVORSTAND_KEINE_DATEN = new ExceptionDataWrapper("401",
            "Die Wahlvorstände konnten über die Schnittstelle nicht geladen werden.");    // technisch (todo kommentar löschen)
    public static final ExceptionDataWrapper POSTWAHLVORSTAND_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("402",
            "postWahlvorstand: Suchkriterien unvollständig.");  // fachlich (todo kommentar löschen)
    public static final ExceptionDataWrapper POSTWAHLVORSTAND_NOT_SAVEABLE = new ExceptionDataWrapper("403",
            "Die Anwesenheiten konnten nicht gespeichert werden. Aktualisieren sie bitte ihre Wahlvorstaende"); // technisch (todo kommentar löschen)
}
