package de.muenchen.oss.wahllokalsystem.broadcastservice.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Authorities {

    public static String SERVICE_POST_MESSAGE = "Broadcast_BUSINESSACTION_Broadcast";
    public static String SERVICE_GET_MESSAGE = "Broadcast_BUSINESSACTION_GetMessage";
    public static String SERVICE_READ_MESSAGE = "Broadcast_BUSINESSACTION_MessageRead";

    public static String REPOSITORY_READ_MESSAGE = "Broadcast_READ_Message";
    public static String REPOSITORY_WRITE_MESSAGE = "Broadcast_WRITE_Message";
    public static String REPOSITORY_DELETE_MESSAGE = "Broadcast_DELETE_Message";

    public static final String[] ALL_BROADCAST_AUTHORITIES = {
            SERVICE_POST_MESSAGE,
            SERVICE_GET_MESSAGE,
            SERVICE_READ_MESSAGE,
            REPOSITORY_READ_MESSAGE,
            REPOSITORY_WRITE_MESSAGE,
            REPOSITORY_DELETE_MESSAGE
    };

    public static final String[] ALL_AUTHORITIES_POST_BROADCAST = {
            SERVICE_POST_MESSAGE,
            REPOSITORY_WRITE_MESSAGE
    };

    public static final String[] ALL_AUTHORITIES_GET_BROADCAST = {
            SERVICE_GET_MESSAGE,
            REPOSITORY_READ_MESSAGE
    };

    public static final String[] ALL_AUTHORITIES_DELETE_BROADCAST = {
            SERVICE_READ_MESSAGE,
            REPOSITORY_DELETE_MESSAGE
    };
}
