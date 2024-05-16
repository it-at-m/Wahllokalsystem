package de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Authorities {

    public static final String SERVICE_GET_KONFIGURATION = "Infomanagement_BUSINESSACTION_GetKonfiguration";
    public static final String SERVICE_POST_KONFIGURATION = "Infomanagement_BUSINESSACTION_PostKonfiguration";
    public static final String SERVICE_GET_KONFIUGRATIONEN = "Infomanagement_BUSINESSACTION_GetKonfigurationen";
    public static final String SERVICE_GET_KENNBUCHSTABEN_LISTEN = "Infomanagement_BUSINESSACTION_GetKennbuchstabenListen";
    public static final String SERVICE_GET_KONFIGURIERTERWAHLTAG = "Infomanagement_BUSINESSACTION_GetKonfigurierterWahltag";
    public static final String SERVICE_POST_KONFIGURIERTERWAHLTAG = "Infomanagement_BUSINESSACTION_PostKonfigurierterWahltag";
    public static final String SERVICE_DELETE_KONFIGURIERTERWAHLTAG = "Infomanagement_BUSINESSACTION_DeleteKonfigurierterWahltag";
    public static final String SERVICE_GET_KONFIGURIERTEWAHLTAGE = "Infomanagement_BUSINESSACTION_GetKonfigurierteWahltage";

    public static final String REPOSITORY_READ_KONFIGURATION = "Infomanagement_READ_Konfiguration";
    public static final String REPOSITORY_DELETE_KONFIGURATION = "Infomanagement_DELETE_Konfiguration";
    public static final String REPOSITORY_WRITE_KONFIGURATION = "Infomanagement_WRITE_Konfiguration";
    public static final String REPOSITORY_READ_KONFIGURIERTERWAHLTAG = "Infomanagement_READ_KonfigurierterWahltag";
    public static final String REPOSITORY_WRITE_KONFIGURIERTERWAHLTAG = "Infomanagement_WRITE_KonfigurierterWahltag";
    public static final String REPOSITORY_DELETE_KONFIGURIERTERWAHLTAG = "Infomanagement_DELETE_KonfigurierterWahltag";
    public static final String REPOSITORY_READ_KONFIGURIERTEWAHLTAGE = "Infomanagement_READ_KonfigurierteWahltage";

    public static final String[] ALL_AUTHORITIES_GET_KONFIGURATION = new String[] {
        SERVICE_GET_KONFIGURATION,
        REPOSITORY_READ_KONFIGURATION
    };

    public static final String[] ALL_AUTHORITIES_SET_KONFIGURATION = new String[] {
        SERVICE_POST_KONFIGURATION,
        REPOSITORY_WRITE_KONFIGURATION
    };

    public static final String[] ALL_AUTHORITIES_GET_KONFIGURATIONS = new String[] {
        SERVICE_GET_KONFIUGRATIONEN,
        REPOSITORY_READ_KONFIGURATION
    };

    public static final String[] ALL_AUTHORITIES_GET_KENNBUCHSTABEN_LISTEN = new String[] {
        SERVICE_GET_KENNBUCHSTABEN_LISTEN, REPOSITORY_READ_KONFIGURATION
    };

    public static final String[] ALL_AUTHORITIES_GET_KONFIGURIERTERWAHLTAG = new String[] {
        SERVICE_GET_KONFIGURIERTERWAHLTAG,
        REPOSITORY_READ_KONFIGURIERTERWAHLTAG
    };

    public static final String[] ALL_AUTHORITIES_SET_KONFIGURIERTERWAHLTAG = new String[] {
        SERVICE_POST_KONFIGURIERTERWAHLTAG,
        REPOSITORY_WRITE_KONFIGURIERTERWAHLTAG
    };

    public static final String[] ALL_AUTHORITIES_DELETE_KONFIGURIERTERWAHLTAG = new String[] {
        SERVICE_DELETE_KONFIGURIERTERWAHLTAG,
        REPOSITORY_DELETE_KONFIGURIERTERWAHLTAG
    };

    public static final String[] ALL_AUTHORITIES_GET_KONFIGURIERTERWAHLTAGE = new String[] {
        SERVICE_GET_KONFIGURIERTEWAHLTAGE,
        REPOSITORY_READ_KONFIGURIERTEWAHLTAGE
    };
}
