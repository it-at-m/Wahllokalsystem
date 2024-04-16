package de.muenchen.oss.wahllokalsystem.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.domain.Message;
import java.time.LocalDateTime;

public class Testdaten {

    public static String WAHLBEZIRK_ID = "123";
    public static String MESSAGE = "Das ist ein Test";
    public static LocalDateTime UHRZEIT = LocalDateTime.now();

    public static Message createMessage(String wahlbezirkID, String nachricht, LocalDateTime time) {
        Message message = new Message();
        message.setEmpfangsZeit(time);
        message.setNachricht(nachricht);
        message.setWahlbezirkID(wahlbezirkID);
        return message;
    }

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
