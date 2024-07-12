package de.muenchen.oss.wahllokalsystem.broadcastservice.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Authorities {

    public static String BROADCAST_BUSINESSACTION_BROADCAST = "Broadcast_BUSINESSACTION_Broadcast";
    public static String BROADCAST_BUSINESSACTION_NACHRICHTABRUFEN = "Broadcast_BUSINESSACTION_GetMessage";
    public static String BROADCAST_BUSINESSACTION_NACHRICHTGELESEN = "Broadcast_BUSINESSACTION_MessageRead";

    public static String BROADCAST_READ_MESSAGE = "Broadcast_READ_Message";
    public static String BROADCAST_WRITE_MESSAGE = "Broadcast_WRITE_Message";
    public static String BROADCAST_DELETE_MESSAGE = "Broadcast_DELETE_Message";

    public static String[] getAllAuthorities() {
        return new String[] {
                BROADCAST_BUSINESSACTION_BROADCAST,
                BROADCAST_BUSINESSACTION_NACHRICHTABRUFEN,
                BROADCAST_BUSINESSACTION_NACHRICHTGELESEN,

                BROADCAST_READ_MESSAGE,
                BROADCAST_WRITE_MESSAGE,
                BROADCAST_DELETE_MESSAGE
        };
    }
}
