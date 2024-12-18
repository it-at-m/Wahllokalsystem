package de.muenchen.oss.wahllokalsystem.authservice.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionConstants {

    public static final String CRYPTO_EXCEPTION_CODE = "399";

    public static ExceptionDataWrapper KOMMUNIKATIONSFEHLER_MIT_KONFIGSERVICE = new ExceptionDataWrapper(
            "100", "Bei der Kommunikation mit dem Konfigurationsservice kam es zu einem Fehler.");
}
