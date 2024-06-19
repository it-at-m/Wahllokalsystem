package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Authorities {

    public static final String SERVICE_GET_URNENWAHLVORBEREITUNG = "Wahlvorbereitung_BUSINESSACTION_GetUrnenwahlVorbereitung";
    public static final String SERVICE_POST_URNENWAHLVORBEREITUNG = "Wahlvorbereitung_BUSINESSACTION_PostUrnenwahlVorbereitung";
    public static final String SERVICE_GET_WAEHLERVERZEICHNIS = "Wahlvorbereitung_BUSINESSACTION_GetWaehlerverzeichnis";
    public static final String SERVICE_POST_WAEHLERVERZEICHNIS = "Wahlvorbereitung_BUSINESSACTION_PostWaehlerverzeichnis";
    public static final String SERVICE_UNTERBRECHUNGSUHRZEIT = "Wahlvorbereitung_BUSINESSACTION_UnterbrechungsUhrzeit";
    public static final String SERVICE_GET_EROEFFNUNGSUHRZEIT = "Wahlvorbereitung_BUSINESSACTION_GetEroeffnungsuhrzeit";
    public static final String SERVICE_POST_EROEFFNUNGSUHRZEIT = "Wahlvorbereitung_BUSINESSACTION_PostEroeffnungsuhrzeit";
    public static final String SERVICE_FORTSETZUNGSUHRZEIT = "Wahlvorbereitung_BUSINESSACTION_FortsetzungsUhrzeit";
    public static final String SERVICE_BRIEFWAHLVORBEREITUNG = "Wahlvorbereitung_BUSINESSACTION_Briefwahlvorbereitung";

    public static final String REPOSITORY_READ_URNENWAHLBORBEREITUNG = "Wahlvorbereitung_READ_UrnenwahlVorbereitung";
    public static final String REPOSITORY_DELETE_URNENWAHLVORBEREITUNG = "Wahlvorbereitung_DELETE_UrnenwahlVorbereitung";
    public static final String REPOSITORY_WRITE_URNENWAHLVORBEREITUNG = "Wahlvorbereitung_WRITE_UrnenwahlVorbereitung";
    public static final String REPOSITORY_READ_WAEHLERVERZEICHNIS = "Wahlvorbereitung_READ_Waehlerverzeichnis";
    public static final String REPOSITORY_WRITE_WAEHLERVERZEICHNIS = "Wahlvorbereitung_WRITE_Waehlerverzeichnis";
    public static final String REPOSITORY_DELETE_WAEHLERVERZEICHNIS = "Wahlvorbereitung_DELETE_Waehlerverzeichnis";
    public static final String REPOSITORY_WRITE_UNTERBRECHUNGSUHRZEIT = "Wahlvorbereitung_WRITE_UnterbrechungsUhrzeit";
    public static final String REPOSITORY_READ_UNTERBRECHUNGSUHRZEIT = "Wahlvorbereitung_READ_UnterbrechungsUhrzeit";
    public static final String REPOSITORY_DELETE_UNTERBRECHUNGSUHRZEIT = "Wahlvorbereitung_DELETE_UnterbrechungsUhrzeit";
    public static final String REPOSITORY_WRITE_EROEFFNUNGSUHRZEIT = "Wahlvorbereitung_WRITE_Eroeffnungsuhrzeit";
    public static final String REPOSITORY_READ_EROEFFNUNGSUHRZEIT = "Wahlvorbereitung_READ_Eroeffnungsuhrzeit";
    public static final String REPOSITORY_DELETE_EROEFFNUNGSUHRZEIT = "Wahlvorbereitung_DELETE_Eroeffnungsuhrzeit";
    public static final String REPOSITORY_WRITE_FORTSETZUNGSUHRZEIT = "Wahlvorbereitung_WRITE_FortsetzungsUhrzeit";
    public static final String REPOSITORY_READ_FORTSETZUNGSUHRZEIT = "Wahlvorbereitung_READ_FortsetzungsUhrzeit";
    public static final String REPOSITORY_DELETE_FORTSETZUNGSUHRZEIT = "Wahlvorbereitung_DELETE_FortsetzungsUhrzeit";
    public static final String REPOSITORY_WRITE_BRIEFWAHLVORBEREITUNG = "Wahlvorbereitung_WRITE_BRIEFWAHLVORBEREITUNG";
    public static final String REPOSITORY_READ_BRIEFWAHLVORBEREITUNG = "Wahlvorbereitung_READ_BRIEFWAHLVORBEREITUNG";
    public static final String REPOSITORY_DELETE_BRIEFWAHLVORBEREITUNG = "Wahlvorbereitung_DELETE_BRIEFWAHLVORBEREITUNG";

    public static final String[] ALL_AUTHORITIES_GET_URNENWAHLVORBEREITUNG = {
            SERVICE_GET_URNENWAHLVORBEREITUNG,
            REPOSITORY_READ_URNENWAHLBORBEREITUNG
    };

    public static final String[] ALL_AUTHORITIES_POST_URNENWAHLVORBEITUNG = {
            SERVICE_POST_URNENWAHLVORBEREITUNG,
            REPOSITORY_WRITE_URNENWAHLVORBEREITUNG
    };

    public static final String[] ALL_AUTHORITIES_GET_WAEHLERVERZEICHNIS = {
            SERVICE_GET_WAEHLERVERZEICHNIS,
            REPOSITORY_READ_WAEHLERVERZEICHNIS
    };

    public static final String[] ALL_AUTHORITIES_POST_WAEHLERVERZEICHNIS = {
            SERVICE_POST_WAEHLERVERZEICHNIS,
            REPOSITORY_WRITE_WAEHLERVERZEICHNIS
    };

    public static final String[] ALL_AUTHORITIES_POST_UNTERBRECHUNGSUHRZEIT = {
            SERVICE_UNTERBRECHUNGSUHRZEIT,
            REPOSITORY_WRITE_UNTERBRECHUNGSUHRZEIT,
            REPOSITORY_READ_UNTERBRECHUNGSUHRZEIT
    };

    public static final String[] ALL_AUTHORITIES_GET_UNTERBRECHUNGSUHRZEIT = {
            SERVICE_UNTERBRECHUNGSUHRZEIT,
            REPOSITORY_READ_UNTERBRECHUNGSUHRZEIT
    };

    public static final String[] ALL_AUTHORITIES_REPO_UNTERBRECHUNGSUHRZEIT = {
            REPOSITORY_READ_UNTERBRECHUNGSUHRZEIT,
            REPOSITORY_DELETE_UNTERBRECHUNGSUHRZEIT,
            REPOSITORY_READ_UNTERBRECHUNGSUHRZEIT
    };

    public static final String[] ALL_AUTHORITIES_POST_EROEFFNUNGSUHRZEIT = {
            SERVICE_POST_EROEFFNUNGSUHRZEIT,
            REPOSITORY_WRITE_EROEFFNUNGSUHRZEIT,
            REPOSITORY_READ_EROEFFNUNGSUHRZEIT
    };

    public static final String[] ALL_AUTHORITIES_GET_EROEFFNUNGSUHRZEIT = {
            SERVICE_GET_EROEFFNUNGSUHRZEIT,
            REPOSITORY_READ_EROEFFNUNGSUHRZEIT
    };

    public static final String[] ALL_AUTHORITIES_REPO_EROEFFNUNGSUHRZEIT = {
            REPOSITORY_READ_EROEFFNUNGSUHRZEIT,
            REPOSITORY_DELETE_EROEFFNUNGSUHRZEIT,
            REPOSITORY_READ_EROEFFNUNGSUHRZEIT
    };

    public static final String[] ALL_AUTHORITIES_POST_FORTSETZUNGSUHRZEIT = {
            SERVICE_FORTSETZUNGSUHRZEIT,
            REPOSITORY_WRITE_FORTSETZUNGSUHRZEIT,
            REPOSITORY_READ_FORTSETZUNGSUHRZEIT
    };

    public static final String[] ALL_AUTHORITIES_GET_FORTSETZUNGSUHRZEIT = {
            SERVICE_FORTSETZUNGSUHRZEIT,
            REPOSITORY_READ_FORTSETZUNGSUHRZEIT
    };

    public static final String[] ALL_AUTHORITIES_REPO_FORTSETZUNGSUHRZEIT = {
            REPOSITORY_READ_FORTSETZUNGSUHRZEIT,
            REPOSITORY_DELETE_FORTSETZUNGSUHRZEIT,
            REPOSITORY_READ_FORTSETZUNGSUHRZEIT
    };

    public static final String[] ALL_AUTHORITIES_POST_BRIEFWAHLVORBEREITUNG = {
            SERVICE_BRIEFWAHLVORBEREITUNG,
            REPOSITORY_WRITE_BRIEFWAHLVORBEREITUNG,
            REPOSITORY_READ_BRIEFWAHLVORBEREITUNG
    };

    public static final String[] ALL_AUTHORITIES_GET_BRIEFWAHLVORBEREITUNG = {
            SERVICE_BRIEFWAHLVORBEREITUNG,
            REPOSITORY_READ_BRIEFWAHLVORBEREITUNG
    };

    public static final String[] ALL_AUTHORITIES_REPO_BRIEFWAHLVORBEREITUNG = {
            REPOSITORY_READ_BRIEFWAHLVORBEREITUNG,
            REPOSITORY_DELETE_BRIEFWAHLVORBEREITUNG,
            REPOSITORY_READ_BRIEFWAHLVORBEREITUNG
    };
}
