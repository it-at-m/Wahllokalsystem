package de.muenchen.oss.wahllokalsystem.briefwahlservice.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionConstants {

    private static final String CODE_GETWAHLBRIEFDATEN_PARAMETER_UNVOLLSTAENDIG = "102";
    private static final String MSG_GETWAHLBRIEFDATEN_PARAMETER_UNVOLLSTAENDIG = "getWahlbriefdaten: Suchkriterien unvollständig.";

    private static final String CODE_POSTWAHLBRIEFDATEN_PARAMETER_UNVOLLSTAENDIG = "103";
    private static final String MSG_POSTWAHLBRIEFDATEN_PARAMETER_UNVOLLSTAENDIG = "postWahlbriefdaten: Suchkriterien unvollständig.";

    public static final ExceptionDataWrapper GETWAHLBRIEFDATEN_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper(
            CODE_GETWAHLBRIEFDATEN_PARAMETER_UNVOLLSTAENDIG, MSG_GETWAHLBRIEFDATEN_PARAMETER_UNVOLLSTAENDIG);
    public static final ExceptionDataWrapper POSTWAHLBRIEFDATEN_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper(
            CODE_POSTWAHLBRIEFDATEN_PARAMETER_UNVOLLSTAENDIG, MSG_POSTWAHLBRIEFDATEN_PARAMETER_UNVOLLSTAENDIG);

}
