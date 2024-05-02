package de.muenchen.oss.wahllokalsystem.briefwahlservice.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionConstants {

    public static final String CODE_GETBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG = "100";
    public static final String CODE_POSTBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG = "101";
    public static final String CODE_GETWAHLBRIEFDATEN_PARAMETER_UNVOLLSTAENDIG = "102";
    public static final String CODE_POSTWAHLBRIEFDATEN_PARAMETER_UNVOLLSTAENDIG = "103";
}
