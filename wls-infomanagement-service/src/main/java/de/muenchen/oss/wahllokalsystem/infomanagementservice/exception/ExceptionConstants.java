package de.muenchen.oss.wahllokalsystem.infomanagementservice.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionConstants {
    public static final ExceptionDataWrapper DELETE_KONFIGURIERTERWAHLTAG_NOT_DELETEABLE = new ExceptionDataWrapper("105",
            "deleteKonfigurierterWahltag: Der konfigurierte Wahltag konnte nicht geloescht werden.");
    public static final ExceptionDataWrapper POST_KONFIGURIERTERWAHLTAG_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("100",
            "postKonfigurierterWahltag: Suchkriterien unvollständig.");
    public static final ExceptionDataWrapper DELETE_KONFIGURIERTERWAHLTAG_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("104",
            "deleteKonfigurierterWahltag: Suchkriterien unvollständig.");
    public static final ExceptionDataWrapper POSTKONFIGURATION_NOT_SAVEABLE = new ExceptionDataWrapper("101",
            "postKonfiguration: Die Konfiguration konnte nicht gespeichert werden");
    public static final ExceptionDataWrapper GETKENNBUCHSTABENLISTEN_KONFIGURATION_NOT_FOUND = new ExceptionDataWrapper("103",
            "getKennbuchstabenListen: Es wurde keinen Kennbuchstaben gefunden.");
}
