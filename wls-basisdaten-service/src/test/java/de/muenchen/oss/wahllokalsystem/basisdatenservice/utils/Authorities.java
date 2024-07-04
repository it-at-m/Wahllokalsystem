package de.muenchen.oss.wahllokalsystem.basisdatenservice.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Authorities {

    public static final String SERVICE_GET_WAHLVORSCHLAEGE = "Basisdaten_BUSINESSACTION_GetWahlvorschlaege";

    public static final String REPOSITORY_READ_WAHLVORSCHLAEGE = "Basisdaten_READ_WLSWahlvorschlaege";
    public static final String REPOSITORY_DELETE_WAHLVORSCHLAEGE = "Basisdaten_DELETE_WLSWahlvorschlaege";
    public static final String REPOSITORY_WRITE_WAHLVORSCHLAEGE = "Basisdaten_WRITE_WLSWahlvorschlaege";

    public static final String REPOSITORY_READ_WAHLVORSCHLAG = "Basisdaten_READ_Wahlvorschlag";
    public static final String REPOSITORY_WRITE_WAHLVORSCHLAG = "Basisdaten_WRITE_Wahlvorschlag";
    public static final String REPOSITORY_DELETE_WAHLVORSCHLAG = "Basisdaten_DELETE_Wahlvorschlag";

    public static final String REPOSITORY_READ_KANDIDAT = "Basisdaten_READ_Kandidat";
    public static final String REPOSITORY_WRITE_KANDIDAT = "Basisdaten_WRITE_Kandidat";
    public static final String REPOSITORY_DELETE_KANDIDAT = "Basisdaten_DELETE_Kandidat";

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

}