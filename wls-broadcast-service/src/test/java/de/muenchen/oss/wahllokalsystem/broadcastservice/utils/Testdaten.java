package de.muenchen.oss.wahllokalsystem.broadcastservice.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.broadcastservice.domain.Message;
import java.time.LocalDateTime;

public class Testdaten {

    public static Message createMessage(String wahlbezirkID, String nachricht, LocalDateTime time) {
        Message message = new Message();
        message.setEmpfangsZeit(time);
        message.setNachricht(nachricht);
        message.setWahlbezirkID(wahlbezirkID);
        return message;
    }

    public static String asJsonString(final Object obj, ObjectMapper mapper) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
