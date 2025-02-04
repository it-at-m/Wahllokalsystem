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
    public static final String SERVICE_GET_BEGRUENDUNG = "Ergebnismeldung_BUSINESSACTION_GetBegruendung";
    public static final String SERVICE_SET_BEGRUENDUNG = "Ergebnismeldung_BUSINESSACTION_PostBegruendung";
    public static final String SERVICE_GET_STIMMZETTELUMSCHLAEGE = "Ergebnismeldung_BUSINESSACTION_GetStimmzettelumschlaege";
    public static final String SERVICE_SET_STIMMZETTELUMSCHLAEGE = "Ergebnismeldung_BUSINESSACTION_PostStimmzettelumschlaege";
    public static final String SERVICE_GET_AUSDRUCK = "Ergebnismeldung_BUSINESSACTION_GetAusdruck";
    public static final String SERVICE_POST_AUSDRUCK = "Ergebnismeldung_BUSINESSACTION_PostAusdruck";
    public static final String SERVICE_GET_STIMMABGABEVERMERKE = "Ergebnismeldung_BUSINESSACTION_GetStimmabgabevermerke";
    public static final String SERVICE_SET_STIMMABGABEVERMERKE = "Ergebnismeldung_BUSINESSACTION_PostStimmabgabevermerke";
    public static final String SERVICE_GET_ERGEBNISSE = "Ergebnismeldung_BUSINESSACTION_GetErgebnisse";
    public static final String SERVICE_SET_ERGEBNISSE = "Ergebnismeldung_BUSINESSACTION_PostErgebnisse";
    public static final String SERVICE_UPDATE_SENDUNGSZEITEN = "Ergebnismeldung_BUSINESSACTION_ForceErgebnisse";
    public static final String SERVICE_SEND_ERGEBNISSE = "Ergebnismeldung_BUSINESSACTION_SendErgebnisse";

    public static final String REPOSITORY_READ_AWERTE = "Ergebnismeldung_READ_AWerte";
    public static final String REPOSITORY_DELETE_AWERTE = "Ergebnismeldung_DELETE_AWerte";
    public static final String REPOSITORY_WRITE_AWERTE = "Ergebnismeldung_WRITE_AWerte";

    public static final String REPOSITORY_READ_STATUS = "Ergebnismeldung_READ_Status";
    public static final String REPOSITORY_DELETE_STATUS = "Ergebnismeldung_DELETE_Status";
    public static final String REPOSITORY_WRITE_STATUS = "Ergebnismeldung_WRITE_Status";

    public static final String REPOSITORY_READ_WAHLSCHEINE = "Ergebnismeldung_READ_Wahlscheine";
    public static final String REPOSITORY_DELETE_WAHLSCHEINE = "Ergebnismeldung_DELETE_Wahlscheine";
    public static final String REPOSITORY_WRITE_WAHLSCHEINE = "Ergebnismeldung_WRITE_Wahlscheine";

    public static final String REPOSITORY_READ_BEGRUENDUNG = "Ergebnismeldung_READ_Begruendung";
    public static final String REPOSITORY_DELETE_BEGRUENDUNG = "Ergebnismeldung_DELETE_Begruendung";
    public static final String REPOSITORY_WRITE_BEGRUENDUNG = "Ergebnismeldung_WRITE_Begruendung";

    public static final String REPOSITORY_READ_STIMMZETTELUMSCHLAEGE = "Ergebnismeldung_READ_Stimmzettelumschlaege";
    public static final String REPOSITORY_DELETE_STIMMZETTELUMSCHLAEGE = "Ergebnismeldung_DELETE_Stimmzettelumschlaege";
    public static final String REPOSITORY_WRITE_STIMMZETTELUMSCHLAEGE = "Ergebnismeldung_WRITE_Stimmzettelumschlaege";

    public static final String REPOSITORY_DELETE_STIMMABGABEVERMERKE = "Ergebnismeldung_DELETE_Stimmabgabevermerke";
    public static final String REPOSITORY_READ_STIMMABGABEVERMERKE = "Ergebnismeldung_READ_Stimmabgabevermerke";
    public static final String REPOSITORY_WRITE_STIMMABGABEVERMERKE = "Ergebnismeldung_WRITE_Stimmabgabevermerke";

    public static final String REPOSITORY_READ_ERGEBNISSE = "Ergebnismeldung_READ_Ergebnisse";
    public static final String REPOSITORY_DELETE_ERGEBNISSE = "Ergebnismeldung_DELETE_Ergebnisse";
    public static final String REPOSITORY_WRITE_ERGEBNISSE = "Ergebnismeldung_WRITE_Ergebnisse";

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

    public static final String[] ALL_AUTHORITIES_SET_WAHLSCHEINE = new String[] {
            SERVICE_SET_WAHLSCHEINE,
            REPOSITORY_WRITE_WAHLSCHEINE
    };

    public static final String[] ALL_AUTHORITIES_GET_BEGRUENDUNG = new String[] {
            REPOSITORY_READ_BEGRUENDUNG,
            SERVICE_GET_BEGRUENDUNG
    };

    public static final String[] ALL_AUTHORITIES_SET_BEGRUENDUNG_MISSING_WILL_RESULT_IN_WLS_EXCEPTION = new String[] {
            REPOSITORY_WRITE_BEGRUENDUNG
    };

    public static final String[] ALL_AUTHORITIES_SET_BEGRUENDUNG_MISSING_WILL_RESULT_IN_ACCESS_DENIED = new String[] {
            REPOSITORY_READ_BEGRUENDUNG,
            SERVICE_SET_BEGRUENDUNG
    };

    public static final String[] ALL_AUTHORITIES_SET_BEGRUENDUNG = ArrayUtils.addAll(ALL_AUTHORITIES_SET_BEGRUENDUNG_MISSING_WILL_RESULT_IN_ACCESS_DENIED,
            ALL_AUTHORITIES_SET_BEGRUENDUNG_MISSING_WILL_RESULT_IN_WLS_EXCEPTION);

    public static final String[] ALL_AUTHORITIES_GET_STIMMZETTELUMSCHLAEGE = new String[] {
            SERVICE_GET_STIMMZETTELUMSCHLAEGE,
            REPOSITORY_READ_STIMMZETTELUMSCHLAEGE
    };

    public static final String[] ALL_AUTHORITIES_SET_STIMMZETTELUMSCHLAEGE = new String[] {
            SERVICE_SET_STIMMZETTELUMSCHLAEGE,
            REPOSITORY_WRITE_STIMMZETTELUMSCHLAEGE
    };

    public static final String[] ALL_AUTHORITIES_GET_STIMMABGABEVERMERKE = new String[] {
            REPOSITORY_READ_STIMMABGABEVERMERKE,
            SERVICE_GET_STIMMABGABEVERMERKE
    };
    public static final String[] ALL_AUTHORITIES_SET_STIMMABGABEVERMERKE = new String[] {
            REPOSITORY_WRITE_STIMMABGABEVERMERKE,
            SERVICE_SET_STIMMABGABEVERMERKE
    };

    public static final String[] ALL_AUTHORITIES_GET_ERGEBNISSE = new String[] {
            REPOSITORY_READ_ERGEBNISSE,
            SERVICE_GET_ERGEBNISSE
    };

    public static final String[] ALL_AUTHORITIES_SET_ERGEBNISSE_MISSING_WILL_RESULT_IN_WLS_EXCEPTION = new String[] {
            REPOSITORY_WRITE_ERGEBNISSE
    };

    public static final String[] ALL_AUTHORITIES_SET_ERGEBNISSE_MISSING_WILL_RESULT_IN_ACCESS_DENIED = new String[] {
            REPOSITORY_READ_ERGEBNISSE,
            SERVICE_SET_ERGEBNISSE
    };

    public static final String[] ALL_AUTHORITIES_SET_ERGEBNISSE = ArrayUtils.addAll(ALL_AUTHORITIES_SET_ERGEBNISSE_MISSING_WILL_RESULT_IN_ACCESS_DENIED,
            ALL_AUTHORITIES_SET_ERGEBNISSE_MISSING_WILL_RESULT_IN_WLS_EXCEPTION);
}
