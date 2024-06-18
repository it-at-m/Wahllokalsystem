package de.muenchen.oss.wahllokalsystem.eaiservice;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Authorities {

    public static final String SERVICE_LOAD_WAHLVORSTAND = "aoueai_BUSINESSACTION_LoadWahlvorstand";
    public static final String SERVICE_SAVE_ANWESENHEIT = "aoueai_BUSINESSACTION_SaveAnwesenheit";

    public static final String[] ALL_AUTHORITIES_GETWAHLVORSTANDFORWAHLBEZIRK = {
            SERVICE_LOAD_WAHLVORSTAND
    };
    public static final String[] ALL_AUTHORIRITES_SETANWESENHEIT = {
            SERVICE_SAVE_ANWESENHEIT
    };
}
