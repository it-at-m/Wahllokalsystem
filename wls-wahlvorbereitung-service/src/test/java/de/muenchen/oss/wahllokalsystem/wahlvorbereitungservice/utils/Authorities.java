package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Authorities {

    public static final String SERVICE_GET_URNENWAHLVORBEREITUNG = "Wahlvorbereitung_BUSINESSACTION_GetUrnenwahlVorbereitung";
    public static final String SERVICE_POST_URNENWAHLVORBEREITUNG = "Wahlvorbereitung_BUSINESSACTION_PostUrnenwahlVorbereitung";

    public static final String REPOSITORY_DELETE_URNENWAHLVORBEREITUNG = "Wahlvorbereitung_DELETE_UrnenwahlVorbereitung";
    public static final String REPOSITORY_WRITE_URNENWAHLVORBEREITUNG = "Wahlvorbereitung_WRITE_UrnenwahlVorbereitung";

    public static final String[] ALL_AUTHORITIES_GET_URNENWAHLVORBEREITUNG = {
            SERVICE_GET_URNENWAHLVORBEREITUNG
    };

    public static final String[] ALL_AUTHORITIES_POST_URNENWAHLVORBEITUNG = {
            SERVICE_POST_URNENWAHLVORBEREITUNG,
            REPOSITORY_WRITE_URNENWAHLVORBEREITUNG
    };
}
