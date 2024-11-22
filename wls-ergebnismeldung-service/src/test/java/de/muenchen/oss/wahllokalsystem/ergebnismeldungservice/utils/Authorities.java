package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Authorities {

    public static final String SERVICE_GET_AWERTE = "Ergebnismeldung_BUSINESSACTION_GetAWerte";

    public static final String REPOSITORY_READ_AWERTE = "Ergebnismeldung_READ_AWerte";
    public static final String REPOSITORY_DELETE_AWERTE = "Ergebnismeldung_DELETE_AWerte";
    public static final String REPOSITORY_WRITE_AWERTE = "Ergebnismeldung_WRITE_AWerte";

    public static final String[] ALL_AUTHORITIES_GET_AWERTE = new String[] {
            SERVICE_GET_AWERTE,
            REPOSITORY_READ_AWERTE,
            REPOSITORY_WRITE_AWERTE
    };
}
