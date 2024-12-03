package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionConstants {

    public static ExceptionDataWrapper GETAWERTE_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("600",
            "getAWerte: Parameter unvollstaendig");

    public static ExceptionDataWrapper GETAWERTE_UNSAVEABLE = new ExceptionDataWrapper("601",
            "getAWerte: Die AWerte vom Client konnten nicht gespeichert werden.");
}
