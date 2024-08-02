package de.muenchen.oss.wahllokalsystem.basisdatenservice.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Authorities {

    public static final String SERVICE_GET_WAHLVORSCHLAEGE = "Basisdaten_BUSINESSACTION_GetWahlvorschlaege";
    public static final String SERVICE_GET_WAHLTAGE = "Basisdaten_BUSINESSACTION_GetWahltage";

    public static final String SERVICE_GET_HANDBUCH = "Basisdaten_BUSINESSACTION_GetHandbuch";
    public static final String SERVICE_POST_HANDBUCH = "Basisdaten_BUSINESSACTION_PostHandbuch";

    public static final String SERVICE_GET_UNGUELTIGEWAHLSCHEINE = "Basisdaten_BUSINESSACTION_GetUngueltigews";
    public static final String SERVICE_POST_UNGUELTIGEWAHLSCHEINE = "Basisdaten_BUSINESSACTION_PostUngueltigews";

    public static final String SERVICE_GET_KOPFDATEN = "Basisdaten_BUSINESSACTION_GetKopfdaten";

    public static final String REPOSITORY_READ_WAHLVORSCHLAEGE = "Basisdaten_READ_WLSWahlvorschlaege";
    public static final String REPOSITORY_DELETE_WAHLVORSCHLAEGE = "Basisdaten_DELETE_WLSWahlvorschlaege";
    public static final String REPOSITORY_WRITE_WAHLVORSCHLAEGE = "Basisdaten_WRITE_WLSWahlvorschlaege";

    public static final String REPOSITORY_READ_WAHLVORSCHLAG = "Basisdaten_READ_Wahlvorschlag";
    public static final String REPOSITORY_WRITE_WAHLVORSCHLAG = "Basisdaten_WRITE_Wahlvorschlag";
    public static final String REPOSITORY_DELETE_WAHLVORSCHLAG = "Basisdaten_DELETE_Wahlvorschlag";

    public static final String REPOSITORY_READ_KANDIDAT = "Basisdaten_READ_Kandidat";
    public static final String REPOSITORY_WRITE_KANDIDAT = "Basisdaten_WRITE_Kandidat";
    public static final String REPOSITORY_DELETE_KANDIDAT = "Basisdaten_DELETE_Kandidat";

    public static final String REPOSITORY_READ_WAHLTAG = "Basisdaten_READ_Wahltag";
    public static final String REPOSITORY_DELETE_WAHLTAG = "Basisdaten_DELETE_Wahltag";
    public static final String REPOSITORY_WRITE_WAHLTAG = "Basisdaten_WRITE_Wahltag";

    public static final String REPOSITORY_READ_HANDBUCH = "Basisdaten_READ_Handbuch";
    public static final String REPOSITORY_WRITE_HANDBUCH = "Basisdaten_WRITE_Handbuch";
    public static final String REPOSITORY_DELETE_HANDBUCH = "Basisdaten_DELETE_Handbuch";

    public static final String REPOSITORY_READ_UNGUELTIGEWAHLSCHEINE = "Basisdaten_READ_Ungueltigews";
    public static final String REPOSITORY_WRITE_UNGUELTIGEWAHLSCHEINE = "Basisdaten_WRITE_Ungueltigews";
    public static final String REPOSITORY_DELETE_UNGUELTIGEWAHLSCHEINE = "Basisdaten_DELETE_Ungueltigews";

    public static final String REPOSITORY_READ_KOPFDATEN = "Basisdaten_READ_Kopfdaten";
    public static final String REPOSITORY_WRITE_KOPFDATEN = "Basisdaten_WRITE_Kopfdaten";
    public static final String REPOSITORY_DELETE_KOPFDATEN = "Basisdaten_DELETE_Kopfdaten";

    public static final String S2S_INFOMANAGEMENT_SERVICE_GET_KONFIGURIERTERWAHLTAG = "Infomanagement_BUSINESSACTION_GetKonfigurierterWahltag";
    public static final String S2S_INFOMANAGEMENT_SERVICE_POST_KONFIGURIERTERWAHLTAG = "Infomanagement_BUSINESSACTION_PostKonfigurierterWahltag";
    public static final String S2S_INFOMANAGEMENT_SERVICE_DELETE_KONFIGURIERTERWAHLTAG = "Infomanagement_BUSINESSACTION_DeleteKonfigurierterWahltag";
    public static final String S2S_INFOMANAGEMENT_REPOSITORY_READ_KONFIGURIERTERWAHLTAG = "Infomanagement_READ_KonfigurierterWahltag";
    public static final String S2S_INFOMANAGEMENT_REPOSITORY_WRITE_KONFIGURIERTERWAHLTAG = "Infomanagement_WRITE_KonfigurierterWahltag";
    public static final String S2S_INFOMANAGEMENT_REPOSITORY_DELETE_KONFIGURIERTERWAHLTAG = "Infomanagement_DELETE_KonfigurierterWahltag";

    public static final String[] ALL_AUTHORITIES_GET_WAHLVORSCHLAEGE = new String[] {
            SERVICE_GET_WAHLVORSCHLAEGE,
            REPOSITORY_READ_WAHLVORSCHLAEGE,
            REPOSITORY_WRITE_WAHLVORSCHLAEGE,
            REPOSITORY_WRITE_WAHLVORSCHLAG,
            REPOSITORY_WRITE_KANDIDAT
    };
    public static final String[] ALL_AUTHORITIES_SET_WAHLVORSCHLAEGE = new String[] {
            SERVICE_GET_WAHLVORSCHLAEGE,
            REPOSITORY_READ_WAHLVORSCHLAEGE,
            REPOSITORY_WRITE_WAHLVORSCHLAEGE
    };

    public static final String[] ALL_AUTHORITIES_DELETE_WAHLVORSCHLAEGE = new String[] {
            REPOSITORY_DELETE_WAHLVORSCHLAEGE
    };
    public static final String[] ALL_AUTHORITIES_GET_HANDBUCH = {
            SERVICE_GET_HANDBUCH,
            REPOSITORY_READ_HANDBUCH,
    };
    public static final String[] ALL_AUTHORITIES_POST_HANDBUCH = {
            SERVICE_POST_HANDBUCH,
            REPOSITORY_WRITE_HANDBUCH
    };

    public static final String[] ALL_AUTHORITIES_GET_WAHLTAGE = new String[] {
            SERVICE_GET_WAHLTAGE,
            REPOSITORY_READ_WAHLTAG,
            REPOSITORY_WRITE_WAHLTAG
    };
    public static final String[] ALL_AUTHORITIES_SET_WAHLTAGE = new String[] {
            SERVICE_GET_WAHLTAGE,
            REPOSITORY_READ_WAHLTAG,
            REPOSITORY_WRITE_WAHLTAG
    };

    public static final String[] ALL_AUTHORITIES_DELETE_WAHLTAGE = new String[] {
            REPOSITORY_DELETE_WAHLTAG
    };

    public static final String[] ALL_AUTHORITIES_GET_UNGUELTIGEWAHLSCHEINE = {
            SERVICE_GET_UNGUELTIGEWAHLSCHEINE,
            REPOSITORY_READ_UNGUELTIGEWAHLSCHEINE
    };
    public static final String[] ALL_AUTHORITIES_POST_UNGUELTIGEWAHLSCHEINE = {
            SERVICE_POST_UNGUELTIGEWAHLSCHEINE,
            REPOSITORY_WRITE_UNGUELTIGEWAHLSCHEINE
    };

    public static final String[] ALL_AUTHORITIES_DELETE_KOPFDATEN = {
            SERVICE_GET_KOPFDATEN,
            REPOSITORY_READ_KOPFDATEN,
            REPOSITORY_WRITE_KOPFDATEN,
            REPOSITORY_DELETE_KOPFDATEN
    };

    public static final String[] ALL_AUTHORITIES_READ_KOPFDATEN = {
            SERVICE_GET_KOPFDATEN,
            REPOSITORY_READ_KOPFDATEN,
            REPOSITORY_WRITE_KOPFDATEN,
            S2S_INFOMANAGEMENT_SERVICE_GET_KONFIGURIERTERWAHLTAG,
            S2S_INFOMANAGEMENT_REPOSITORY_READ_KONFIGURIERTERWAHLTAG,
    };

    public static final String[] ALL_AUTHORITIES_KOPFDATEN = {
            SERVICE_GET_KOPFDATEN,
            REPOSITORY_READ_KOPFDATEN,
            REPOSITORY_WRITE_KOPFDATEN,
            S2S_INFOMANAGEMENT_SERVICE_GET_KONFIGURIERTERWAHLTAG,
            S2S_INFOMANAGEMENT_SERVICE_POST_KONFIGURIERTERWAHLTAG,
            S2S_INFOMANAGEMENT_SERVICE_DELETE_KONFIGURIERTERWAHLTAG,
            S2S_INFOMANAGEMENT_REPOSITORY_READ_KONFIGURIERTERWAHLTAG,
            S2S_INFOMANAGEMENT_REPOSITORY_WRITE_KONFIGURIERTERWAHLTAG,
            S2S_INFOMANAGEMENT_REPOSITORY_DELETE_KONFIGURIERTERWAHLTAG
    };
}
