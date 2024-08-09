package de.muenchen.oss.wahllokalsystem.basisdatenservice.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Authorities {

    public static final String SERVICE_GET_WAHLVORSCHLAEGE = "Basisdaten_BUSINESSACTION_GetWahlvorschlaege";
    public static final String SERVICE_GET_WAHLTAGE = "Basisdaten_BUSINESSACTION_GetWahltage";

    public static final String SERVICE_RESET_WAHLEN = "Basisdaten_BUSINESSACTION_ResetWahlen";
    public static final String SERVICE_GET_WAHLEN = "Basisdaten_BUSINESSACTION_GetWahlen";
    public static final String SERVICE_POST_WAHLEN = "Basisdaten_BUSINESSACTION_PostWahlen";

    public static final String SERVICE_GET_HANDBUCH = "Basisdaten_BUSINESSACTION_GetHandbuch";
    public static final String SERVICE_POST_HANDBUCH = "Basisdaten_BUSINESSACTION_PostHandbuch";

    public static final String SERVICE_GET_UNGUELTIGEWAHLSCHEINE = "Basisdaten_BUSINESSACTION_GetUngueltigews";
    public static final String SERVICE_POST_UNGUELTIGEWAHLSCHEINE = "Basisdaten_BUSINESSACTION_PostUngueltigews";

    public static final String SERVICE_GET_REFERENDUMVORLAGEN = "Basisdaten_BUSINESSACTION_GetReferendumvorlagen";

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

    public static final String REPOSITORY_READ_WAHL = "Basisdaten_READ_Wahl";
    public static final String REPOSITORY_WRITE_WAHL = "Basisdaten_WRITE_Wahl";
    public static final String REPOSITORY_DELETE_WAHL = "Basisdaten_DELETE_Wahl";

    public static final String REPOSITORY_READ_UNGUELTIGEWAHLSCHEINE = "Basisdaten_READ_Ungueltigews";
    public static final String REPOSITORY_WRITE_UNGUELTIGEWAHLSCHEINE = "Basisdaten_WRITE_Ungueltigews";
    public static final String REPOSITORY_DELETE_UNGUELTIGEWAHLSCHEINE = "Basisdaten_DELETE_Ungueltigews";

    public static final String REPOSITORY_READ_REFERENDUMVORLAGEN = "Basisdaten_READ_Referendumvorlagen";
    public static final String REPOSITORY_WRITE_REFERENDUMVORLAGEN = "Basisdaten_WRITE_Referendumvorlagen";
    public static final String REPOSITORY_DELETE_REFERENDUMVORLAGEN = "Basisdaten_DELETE_Referendumvorlagen";

    public static final String REPOSITORY_READ_REFERENDUMVORLAGE = "Basisdaten_READ_Referendumvorlage";
    public static final String REPOSITORY_WRITE_REFERENDUMVORLAGE = "Basisdaten_WRITE_Referendumvorlage";
    public static final String REPOSITORY_DELETE_REFERENDUMVORLAGE = "Basisdaten_DELETE_Referendumvorlage";

    public static final String[] ALL_AUTHORITIES_GET_WAHLVORSCHLAEGE = new String[] {
            SERVICE_GET_WAHLVORSCHLAEGE,
            REPOSITORY_READ_WAHLVORSCHLAEGE,
            REPOSITORY_WRITE_WAHLVORSCHLAEGE,
            REPOSITORY_WRITE_WAHLVORSCHLAG,
            REPOSITORY_WRITE_KANDIDAT
    };
    public static final String[] ALL_AUTHORITIES_SET_WAHLVORSCHLAEGE = {
            SERVICE_GET_WAHLVORSCHLAEGE,
            REPOSITORY_READ_WAHLVORSCHLAEGE,
            REPOSITORY_WRITE_WAHLVORSCHLAEGE
    };

    public static final String[] ALL_AUTHORITIES_DELETE_WAHLVORSCHLAEGE = {
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

    public static final String[] ALL_AUTHORITIES_GET_WAHLTAGE = {
            SERVICE_GET_WAHLTAGE,
            REPOSITORY_READ_WAHLTAG,
            REPOSITORY_WRITE_WAHLTAG
    };
    public static final String[] ALL_AUTHORITIES_SET_WAHLTAGE = {
            SERVICE_GET_WAHLTAGE,
            REPOSITORY_READ_WAHLTAG,
            REPOSITORY_WRITE_WAHLTAG
    };

    public static final String[] ALL_AUTHORITIES_DELETE_WAHLTAGE = {
            REPOSITORY_DELETE_WAHLTAG
    };

    public static final String[] ALL_AUTHORITIES_RESET_WAHLEN = {
            SERVICE_RESET_WAHLEN,
            REPOSITORY_READ_WAHL,
            REPOSITORY_WRITE_WAHL
    };

    public static final String[] ALL_AUTHORITIES_GET_UNGUELTIGEWAHLSCHEINE = {
            SERVICE_GET_UNGUELTIGEWAHLSCHEINE,
            REPOSITORY_READ_UNGUELTIGEWAHLSCHEINE
    };

    public static final String[] ALL_AUTHORITIES_POST_UNGUELTIGEWAHLSCHEINE = {
            SERVICE_POST_UNGUELTIGEWAHLSCHEINE,
            REPOSITORY_WRITE_UNGUELTIGEWAHLSCHEINE
    };

    public static final String[] ALL_AUTHORITIES_WAHLEN = {
            SERVICE_RESET_WAHLEN,
            SERVICE_GET_WAHLEN,
            SERVICE_POST_WAHLEN,
            REPOSITORY_READ_WAHL,
            REPOSITORY_WRITE_WAHL,
            REPOSITORY_DELETE_WAHL
    };

    public static final String[] ALL_AUTHORITIES_GET_REFERENDUMVORLAGEN = {
            SERVICE_GET_REFERENDUMVORLAGEN,
            REPOSITORY_READ_REFERENDUMVORLAGEN,
            REPOSITORY_WRITE_REFERENDUMVORLAGEN,
            REPOSITORY_WRITE_REFERENDUMVORLAGE
    };
}
