package de.muenchen.oss.wahllokalsystem.adminservice.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionConstants {

    public static final ExceptionDataWrapper KOMMUNIKATIONSFEHLER_MIT_BASISDATEN = new ExceptionDataWrapper(
            "200", "Bei der Kommunikation mit dem Basisdaten-Service kam es zu einem Fehler.");
    public static final ExceptionDataWrapper KOMMUNIKATIONSFEHLER_MIT_ERGEBNISMELDUNG = new ExceptionDataWrapper(
            "500", "Bei der Kommunikation mit dem Ergebnismeldung-Service kam es zu einem Fehler.");
    public static final ExceptionDataWrapper KOMMUNIKATIONSFEHLER_MIT_INFOMANAGEMENT = new ExceptionDataWrapper(
            "300", "Bei der Kommunikation mit dem Infomanagement-Service kam es zu einem Fehler.");

    public static final ExceptionDataWrapper INVALID_ARGUMENT = new ExceptionDataWrapper(
            "166", "Kein Wahltag vorhanden für die angegebene Wahltag-ID");
    public static final ExceptionDataWrapper MISSING_ARGUMENT = new ExceptionDataWrapper(
            "165", "Parameter fehlt.");
}
