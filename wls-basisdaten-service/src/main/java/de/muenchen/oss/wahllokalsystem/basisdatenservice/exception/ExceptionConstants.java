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

    public static ExceptionDataWrapper FAILED_COMMUNICATION_WITH_SERVICE = new ExceptionDataWrapper("101",
            "Bei der Kommunikation mit einem anderen Service ist ein Fehler aufgetreten.");

    public static ExceptionDataWrapper GETHANDBUCH_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("301", "getHandbuch: Suchkriterien unvollständig.");
    public static ExceptionDataWrapper GETHANDBUCH_KEINE_DATEN = new ExceptionDataWrapper("302", "Das Handbuch konnte nicht geladen werden.");

    public static ExceptionDataWrapper POSTHANDBUCH_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("315", "postHandbuch: Suchkriterien unvollständig.");
    public static ExceptionDataWrapper POSTHANDBUCH_SPEICHERN_NICHT_ERFOLGREICH = new ExceptionDataWrapper("316",
            "postHandbuch: Das speichern des Handbuches war nicht erfolgreich.");
    public static ExceptionDataWrapper RESET_WAHLEN_NICHT_ERFOLGREICH = new ExceptionDataWrapper("317",
            "resetWahlen: Das Zurücksetzen der Wahlen war nicht erfolgreich.");

    public static ExceptionDataWrapper GETUNGUELTIGEWAHLSCHEINE_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("331",
            "getUngueltigews: Suchkriterien unvollständig.");
    public static ExceptionDataWrapper GETUNGUELTIGEWAHLSCHEINE_KEINE_DATEN = new ExceptionDataWrapper("332",
            "Die ungueltigen Wahlscheine konnte nicht geladen werden.");

    public static ExceptionDataWrapper POSTUNGUELTIGEWS_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("345",
            "postUngueltigews: Suchkriterien unvollständig.");
    public static ExceptionDataWrapper POSTUNGUELTIGEWS_SPEICHERN_NICHT_ERFOLGREICH = new ExceptionDataWrapper("346",
            "postUngueltigews: Das speichern der ungueltigen Wahlscheine war nicht erfolgreich.");

    public static ExceptionDataWrapper GETREFERENDUMVORLAGEN_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("308",
            "getReferendumvorlagen: Suchkriterien unvollständig.");

    public static ExceptionDataWrapper WAHL_SPEICHERN_FEHLGESCHLAGEN = new ExceptionDataWrapper("351",
            "postUngueltigeWahl: Das Speichern der Wahl ist fehlgeschlagen");

    public static ExceptionDataWrapper CODE_GETWAHLEN_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("314",
            "getWahlen: Der Wahltag zu den gesuchten Wahlen existiert nicht.");
    public static ExceptionDataWrapper CODE_POSTWAHLEN_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("317",
            "postWahlen: Suchkriterien unvollständig. Die wahltagID ist leer oder null.");
    public static ExceptionDataWrapper CODE_POSTWAHLEN_UNSAVEABLE = new ExceptionDataWrapper("318",
            "postWahlen: Die Wahlen konnten nicht gespeichert werden.");
    public static ExceptionDataWrapper CODE_GETWAHLBEZIRKE_NO_WAHLTAG = new ExceptionDataWrapper("313",
            "getWahlen: Es wurde kein Wahltag zu dieser WahltagID gefunden. Bitte legen Sie die Basisdaten an.");

    public static final ExceptionDataWrapper GETKOPFDATEN_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("303",
            "getKopfdaten: Suchkriterien unvollständig.");
    public static final ExceptionDataWrapper GETKOPFDATEN_NO_KONFIGURIERTERWAHLTAG = new ExceptionDataWrapper("304",
            "getKopfdaten: Es wurde kein KonfigurierterWahltag gefunden.");
    public static final ExceptionDataWrapper GETKOPFDATEN_NO_BASISDATEN = new ExceptionDataWrapper("305", "getKopfdaten: Es wurden keine Basisdaten gefunden.");
    public static final ExceptionDataWrapper INITIALIZE_KOPFDATEN_NO_BASISSTRUKTURDATEN = new ExceptionDataWrapper("322",
            "initializeKopfdaten: Es wurden keine Basisstrukturdaten gefunden.");
    public static final ExceptionDataWrapper INITIALIZE_KOPFDATEN_NO_WAHL_WAHLBEZIRK_STIMMZETTELGEBIET = new ExceptionDataWrapper("321",
            "initializeKopfdaten: Die vorhanden Daten führen zu keinem richtigen Ergebnis, bitte überprüfen sie die IVU Konfiguration");
    public static final ExceptionDataWrapper INITIALIZE_KOPFDATEN_BASISDATEN_DATA_INCONSISTENTCY = new ExceptionDataWrapper("323",
            "BasisdatenValidierung: Es gab Inkonsistenzen in den Bestandteilen der Basisdaten. Basisstrukturdaten, Wahlen, Wahlbezirke, Stimmzettelgebiete konnten nicht eindeutig einander zugeordnetwerden.");
}
