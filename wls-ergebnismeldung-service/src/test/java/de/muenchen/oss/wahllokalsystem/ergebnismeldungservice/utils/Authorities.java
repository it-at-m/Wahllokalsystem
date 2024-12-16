package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Authorities {

    public static final String SERVICE_GET_AWERTE = "Ergebnismeldung_BUSINESSACTION_GetAWerte";
    public static final String ADMIN_LOADWAHLTERMINDATEN = "Admin_BUSINESSACTION_LoadWahltermindaten";
    public static final String SERVICE_GET_STATUS = "Ergebnismeldung_BUSINESSACTION_GetStatus";
    public static final String SERVICE_SET_STATUS = "Ergebnismeldung_BUSINESSACTION_PostStatus";
    public static final String SERVICE_GET_WAHLSCHEINE = "Ergebnismeldung_BUSINESSACTION_GetWahlscheine";
    public static final String SERVICE_SET_WAHLSCHEINE = "Ergebnismeldung_BUSINESSACTION_PostWahlscheine";

    public static final String REPOSITORY_READ_AWERTE = "Ergebnismeldung_READ_AWerte";
    public static final String REPOSITORY_DELETE_AWERTE = "Ergebnismeldung_DELETE_AWerte";
    public static final String REPOSITORY_WRITE_AWERTE = "Ergebnismeldung_WRITE_AWerte";

    public static final String REPOSITORY_READ_STATUS = "Ergebnismeldung_READ_Status";
    public static final String REPOSITORY_DELETE_STATUS = "Ergebnismeldung_DELETE_Status";
    public static final String REPOSITORY_WRITE_STATUS = "Ergebnismeldung_WRITE_Status";

    public static final String REPOSITORY_READ_WAHLSCHEINE = "Ergebnismeldung_READ_WAHLSCHEINE";
    public static final String REPOSITORY_DELETE_WAHLSCHEINE = "Ergebnismeldung_DELETE_WAHLSCHEINE";
    public static final String REPOSITORY_WRITE_WAHLSCHEINE = "Ergebnismeldung_WRITE_WAHLSCHEINE";

    public static final String[] ALL_AUTHORITIES_USER_GET_AWERTE = new String[] {
            SERVICE_GET_AWERTE,
            REPOSITORY_READ_AWERTE,
            REPOSITORY_WRITE_AWERTE
    };

    public static final String[] ALL_AUTHORITIES_ADMIN_GET_AWERTE = new String[] {
            ADMIN_LOADWAHLTERMINDATEN,
            REPOSITORY_READ_AWERTE,
            REPOSITORY_WRITE_AWERTE
    };

    public static final String[] ALL_AUTHORITIES_GET_STATUS = new String[] {
            SERVICE_GET_STATUS,
            REPOSITORY_READ_STATUS
    };

    public static final String[] ALL_AUTHORITIES_SET_STATUS_MISSING_WILL_RESULT_IN_WLS_EXCEPTION = new String[] {
            REPOSITORY_WRITE_STATUS
    };

    public static final String[] ALL_AUTHORITIES_SET_STATUS_MISSING_WILL_RESULT_IN_ACCESS_DENIED = new String[] {
            REPOSITORY_READ_STATUS,
            SERVICE_SET_STATUS
    };

    public static final String[] ALL_AUTHORITIES_SET_STATUS = ArrayUtils.addAll(ALL_AUTHORITIES_SET_STATUS_MISSING_WILL_RESULT_IN_ACCESS_DENIED,
            ALL_AUTHORITIES_SET_STATUS_MISSING_WILL_RESULT_IN_WLS_EXCEPTION);

    public static final String[] ALL_AUTHORITIES_GET_WAHLSCHEINE = new String[] {
        SERVICE_GET_WAHLSCHEINE,
        REPOSITORY_READ_WAHLSCHEINE
    };

    public static final String[] ALL_AUTHORITIES_SET_WAHLSCHEINE_MISSING_WILL_RESULT_IN_WLS_EXCEPTION = new String[] {
        REPOSITORY_WRITE_WAHLSCHEINE
    };

    public static final String[] ALL_AUTHORITIES_SET_WAHLSCHEINE_MISSING_WILL_RESULT_IN_ACCESS_DENIED = new String[] {
        REPOSITORY_READ_WAHLSCHEINE,
        SERVICE_SET_WAHLSCHEINE
    };

    public static final String[] ALL_AUTHORITIES_SET_WAHLSCHEINE = ArrayUtils.addAll(ALL_AUTHORITIES_SET_WAHLSCHEINE_MISSING_WILL_RESULT_IN_ACCESS_DENIED,
        ALL_AUTHORITIES_SET_WAHLSCHEINE_MISSING_WILL_RESULT_IN_WLS_EXCEPTION);

}
