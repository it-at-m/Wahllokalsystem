package de.muenchen.oss.wahllokalsystem.adminservice.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionConstants {

    public static final ExceptionDataWrapper KOMMUNIKATIONSFEHLER_MIT_BASISDATEN = new ExceptionDataWrapper(
        "200", "Bei der Kommunikation mit dem Basisdaten-Service kam es zu einem Fehler: %s");

    public static ExceptionDataWrapper MISSING_ARGUMENT = new ExceptionDataWrapper(
        "165", "Parameter %s fehlt.");
}
