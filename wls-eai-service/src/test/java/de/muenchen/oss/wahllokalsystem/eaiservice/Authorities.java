package de.muenchen.oss.wahllokalsystem.eaiservice;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Authorities {

    public static final String SERVICE_LOAD_WAHLVORSTAND = "aoueai_BUSINESSACTION_LoadWahlvorstand";
    public static final String SERVICE_SAVE_ANWESENHEIT = "aoueai_BUSINESSACTION_SaveAnwesenheit";
    public static final String SERVICE_LOAD_WAHLVORSCHLAEGE = "aoueai_BUSINESSACTION_LoadWahlvorschlaege";
    public static final String SERVICE_LOAD_WAHLVORSCHLAEGELISTE = "aoueai_BUSINESSACTION_LoadWahlvorschlaegeListe";
    public static final String SERVICE_LOAD_REFERENDUMVORLAGEN = "aoueai_BUSINESSACTION_LoadReferendumvorlagen";

    public static final String SERVICE_LOAD_WAHLBERECHTIGTE = "aoueai_BUSINESSACTION_LoadWahlberechtigte";
    public static final String SERVICE_LOAD_WAHLTAGE = "aoueai_BUSINESSACTION_LoadWahltage";
    public static final String SERVICE_LOAD_WAHLBEZIRKE = "aoueai_BUSINESSACTION_LoadWahlbezirke";
    public static final String SERVICE_LOAD_WAHLEN = "aoueai_BUSINESSACTION_LoadWahlen";
    public static final String SERVICE_LOAD_BASISDATEN = "aoueai_BUSINESSACTION_LoadBasisdaten";

    public static final String SERVICE_SAVE_WAHLBETEILIGUNG = "aoueai_BUSINESSACTION_SaveWahlbeteiligung";

    public static final String[] ALL_AUTHORITIES_GETWAHLVORSTANDFORWAHLBEZIRK = {
        SERVICE_LOAD_WAHLVORSTAND
    };
    public static final String[] ALL_AUTHORIRITES_SETANWESENHEIT = {
        SERVICE_SAVE_ANWESENHEIT
    };
    public static final String[] ALL_AUTHORITIES_GETWAHLVORSCHLAEGE = {
        SERVICE_LOAD_WAHLVORSCHLAEGE,
    };
    public static final String[] ALL_AUTHORITIES_GETWAHLVORSCHLAEGELISTE = {
        SERVICE_LOAD_WAHLVORSCHLAEGELISTE,
    };
    public static final String[] ALL_AUTHORITIES_GETREFERENDUMVORLAGEN = {
        SERVICE_LOAD_REFERENDUMVORLAGEN,
    };
}
