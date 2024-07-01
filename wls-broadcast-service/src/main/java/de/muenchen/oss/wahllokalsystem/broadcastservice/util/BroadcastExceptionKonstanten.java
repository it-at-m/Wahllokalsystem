package de.muenchen.oss.wahllokalsystem.broadcastservice.util;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionKonstanten;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BroadcastExceptionKonstanten {

    public static final String CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG = "150";

    public static final ExceptionDataWrapper BROADCAST_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper(CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG,
            "Das Object BroadcastMessage ist nicht vollständig.");

    public static final ExceptionDataWrapper BROADCAST_ENTITY_NOT_FOUND = new ExceptionDataWrapper(ExceptionKonstanten.CODE_ENTITY_NOT_FOUND,
            "No message found");

    public static final ExceptionDataWrapper BROADCAST_PARAMETER_UNVOLLSTAENDIG_EMPTY_WAHLBEZIRKID = new ExceptionDataWrapper(CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG,
            "wahlbezirkID is blank or empty");

    public static final ExceptionDataWrapper BROADCAST_PARAMETER_UNVOLLSTAENDIG_EMPTY_NACHRICHTID = new ExceptionDataWrapper(CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG,
            "nachrichtID is blank or empty");

    public static final ExceptionDataWrapper BROADCAST_PARAMETER_UNVOLLSTAENDIG_BAD_FORMAT_UUID = new ExceptionDataWrapper(CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG,
            "Nachricht-UUID bad format");

}
