package de.muenchen.oss.wahllokalsystem.adminservice.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Authorities {

    public static final String ADMIN_LOADWAHLTERMINDATEN = "Admin_BUSINESSACTION_LoadWahltermindaten";
    public static final String ADMIN_GETWAHLEN = "Admin_BUSINESSACTION_GetWahlen";
    public static final String ADMIN_UPDATEWAHLEN = "Admin_BUSINESSACTION_UpdateWahlen";
    public static final String ADMIN_GETWAHLTAGE = "Admin_BUSINESSACTION_GetWahltage";
    public static final String ADMIN_DELETEWAHLTERMINDATEN = "Admin_BUSINESSACTION_DeleteWahltermindaten";

    public static final String[] ALL_AUTHORITIES_UPDATEWAHLEN = new String[] {
            ADMIN_UPDATEWAHLEN
    };

    public static final String[] ALL_AUTHORITIES_GETWAHLEN = new String[] {
            ADMIN_GETWAHLEN
    };
}
