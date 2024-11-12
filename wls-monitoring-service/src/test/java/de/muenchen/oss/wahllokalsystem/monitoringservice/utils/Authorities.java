package de.muenchen.oss.wahllokalsystem.monitoringservice.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Authorities {

    public static final String SERVICE_GET_WAEHLERANZAHL = "Monitoring_BUSINESSACTION_GetWahlbeteiligung";
    public static final String SERVICE_POST_WAEHLERANZAHL = "Monitoring_BUSINESSACTION_PostWahlbeteiligung";

    public static final String REPOSITORY_READ_WAEHLERANZAHL = "Monitoring_READ_Waehleranzahl";
    public static final String REPOSITORY_DELETE_WAEHLERANZAHL = "Monitoring_DELETE_Waehleranzahl";
    public static final String REPOSITORY_WRITE_WAEHLERANZAHL = "Monitoring_WRITE_Waehleranzahl";

    public static final String[] ALL_AUTHORITIES_GET_WAEHLERANZAHL = new String[] {
            SERVICE_GET_WAEHLERANZAHL,
            REPOSITORY_READ_WAEHLERANZAHL
    };
    public static final String[] ALL_AUTHORITIES_SET_WAEHLERANZAHL = new String[] {
            SERVICE_POST_WAEHLERANZAHL,
            REPOSITORY_WRITE_WAEHLERANZAHL
    };

    public static final String SERVICE_POST_LASTSEEN = "Monitoring_BUSINESSACTION_PostLastSeen";
    public static final String SERVICE_POST_LAST_LOGOUT = "Monitoring_BUSINESSACTION_PostLetzteAbmeldung";
    public static final String SERVICE_POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT = "Monitoring_BUSINESSACTION_PostSchnellmeldungSendungsuhrzeit";
    public static final String SERVICE_POST_SCHNELLMELDUNG_DRUCKUHRZEIT = "Monitoring_BUSINESSACTION_PostSchnellmeldungDruckuhrzeit";
    public static final String SERVICE_POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT = "Monitoring_BUSINESSACTION_PostNiederschriftSendungsuhrzeit";
    public static final String SERVICE_POST_NIEDERSCHRIFT_DRUCKUHRZEIT = "Monitoring_BUSINESSACTION_PostNiederschriftDruckuhrzeit";

}
