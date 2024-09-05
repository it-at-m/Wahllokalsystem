package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Authorities {

    public static final String SERVICE_GET_EREIGNISSE = "VorfaelleUndVorkommnisse_BUSINESSACTION_GetEreignisse";
    public static final String SERVICE_POST_EREIGNISSE = "VorfaelleUndVorkommnisse_BUSINESSACTION_PostEreignisse";

    public static final String REPOSITORY_READ_EREIGNISSE = "VorfaelleUndVorkommnisse_READ_Ereignisse";
    public static final String REPOSITORY_WRITE_EREIGNISSE = "VorfaelleUndVorkommnisse_WRITE_Ereignisse";
    public static final String REPOSITORY_DELETE_EREIGNISSE = "VorfaelleUndVorkommnisse_DELETE_Ereignisse";

    public static final String[] ALL_REPO_AUTHORITIES_GET_EREIGNISSE = {
            REPOSITORY_READ_EREIGNISSE
    };

    public static final String[] ALL_SERVICE_AUTHORITIES_GET_EREIGNISSE = {
            SERVICE_GET_EREIGNISSE
    };

    public static final String[] ALL_AUTHORITIES_GET_EREIGNISSE = ArrayUtils.addAll(ALL_REPO_AUTHORITIES_GET_EREIGNISSE, ALL_SERVICE_AUTHORITIES_GET_EREIGNISSE);

    public static final String[] ALL_REPO_AUTHORITIES_SET_EREIGNISSE = {
            REPOSITORY_WRITE_EREIGNISSE
    };

    public static final String[] ALL_SERVICE_AUTHORITIES_SET_EREIGNISSE = {
            SERVICE_POST_EREIGNISSE
    };

    public static final String[] ALL_AUTHORITIES_SET_EREIGNISSE = ArrayUtils.addAll(ALL_REPO_AUTHORITIES_SET_EREIGNISSE, ALL_SERVICE_AUTHORITIES_SET_EREIGNISSE);
}
