package de.muenchen.oss.wahllokalsystem.infomanagementservice.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Authorities {

    public static final String SERVICE_GET_KONFIGURATION = "Infomanagement_BUSINESSACTION_GetKonfiguration";

    public static final String REPOSITORY_READ_KONFIGURATION = "Infomanagement_READ_Konfiguration";

    public static final String[] ALL_AUTHORITIES_GET_KONFIGURATION = new String[] {
            SERVICE_GET_KONFIGURATION,
            REPOSITORY_READ_KONFIGURATION
    };
}
