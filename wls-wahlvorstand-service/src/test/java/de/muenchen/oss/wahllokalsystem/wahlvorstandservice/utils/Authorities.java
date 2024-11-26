package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Authorities {

    public static final String SERVICE_GET_WAHLVORSTAND = "Wahlvorstand_BUSINESSACTION_GetWahlvorstand";
    public static final String SERVICE_POST_WAHLVORSTAND = "Wahlvorstand_BUSINESSACTION_PostWahlvorstand";
    public static final String SERVICE_UPDATE_WAHLVORSTAND = "Wahlvorstand_BUSINESSACTION_UpdateWahlvorstand";

    public static final String REPOSITORY_READ_WAHLVORSTAND = "Wahlvorstand_READ_Wahlvorstand";
    public static final String REPOSITORY_WRITE_WAHLVORSTAND = "Wahlvorstand_WRITE_Wahlvorstand";
    public static final String REPOSITORY_DELETE_WAHLVORSTAND = "Wahlvorstand_DELETE_Wahlvorstand";

    public static final String[] ALL_REPO_AUTHORITIES_GET_WAHLVORSTAND = {
            REPOSITORY_READ_WAHLVORSTAND
    };

    public static final String[] ALL_SERVICE_AUTHORITIES_GET_WAHLVORSTAND = {
            SERVICE_GET_WAHLVORSTAND,
    };

    public static final String[] ALL_AUTHORITIES_GET_WAHLVORSTAND = ArrayUtils.addAll(ALL_REPO_AUTHORITIES_GET_WAHLVORSTAND,
            ALL_SERVICE_AUTHORITIES_GET_WAHLVORSTAND);

    public static final String[] ALL_REPO_AUTHORITIES_POST_WAHLVORSTAND = {
            REPOSITORY_WRITE_WAHLVORSTAND
    };

    public static final String[] ALL_SERVICE_AUTHORITIES_POST_WAHLVORSTAND = {
            SERVICE_POST_WAHLVORSTAND
    };

    public static final String[] ALL_AUTHORITIES_POST_WAHLVORSTAND = ArrayUtils.addAll(ALL_REPO_AUTHORITIES_POST_WAHLVORSTAND,
            ALL_SERVICE_AUTHORITIES_POST_WAHLVORSTAND);
}
