package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionKonstanten;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionConstants {

    public static ExceptionDataWrapper FAILED_COMMUNICATION_WITH_EAI = new ExceptionDataWrapper("100",
        "Bei der Kommunikation mit dem Aoueai-Service ist ein Fehler aufgetreten. Es konnten daher keine Daten geladen werden.");

    public static ExceptionDataWrapper NULL_FROM_CLIENT = new ExceptionDataWrapper(ExceptionKonstanten.CODE_ENTITY_NOT_FOUND, "not found");

    public static ExceptionDataWrapper GETAWERTE_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("600",
        "getAWerte: Parameter unvollstaendig");

    public static ExceptionDataWrapper GETAWERTE_UNSAVEABLE = new ExceptionDataWrapper("601",
        "getAWerte: Die AWerte aus der IVU konnten nicht gespeichert werden.");
}
