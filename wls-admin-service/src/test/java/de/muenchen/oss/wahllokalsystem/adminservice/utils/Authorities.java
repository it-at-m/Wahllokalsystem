package de.muenchen.oss.wahllokalsystem.adminservice.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Authorities {

    public static final String ADMIN_LOADWAHLTERMINDATEN = "Admin_BUSINESSACTION_LoadWahltermindaten";

    public static final String ADMIN_READ_KONFIGURIERTEWAHLTAGE = "Admin_BUSINESSACTION_GetKonfigurierteWahltage";

}
