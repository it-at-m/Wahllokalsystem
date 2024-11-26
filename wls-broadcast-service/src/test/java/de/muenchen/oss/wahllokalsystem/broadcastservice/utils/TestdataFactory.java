package de.muenchen.oss.wahllokalsystem.broadcastservice.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.broadcastservice.domain.Message;
import de.muenchen.oss.wahllokalsystem.broadcastservice.rest.BroadcastMessageDTO;
import de.muenchen.oss.wahllokalsystem.broadcastservice.rest.MessageDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TestdataFactory {

    public static class CreateMessageEntity {

        public static Message withCustomParams(String wahlbezirkID, String nachricht, LocalDateTime time) {
            Message message = new Message();
            message.setEmpfangsZeit(time);
            message.setNachricht(nachricht);
            message.setWahlbezirkID(wahlbezirkID);
            return message;
        }

        public static List<Message> listFromBroadcastMessageDto(BroadcastMessageDTO broadcastMessageDTO, LocalDateTime empfangsZeit) {
            return broadcastMessageDTO.wahlbezirkIDs() // TODO: darf die oid null sein?
                    .stream().map(wahlbezirkId -> new Message(null, wahlbezirkId, broadcastMessageDTO.nachricht(), empfangsZeit))
                    .toList();
        }
    }

    public static class CreateMessageDto {

        public static MessageDTO fromEntity(Message message) {
            return new MessageDTO(message.getOid(), message.getWahlbezirkID(), message.getNachricht(), message.getEmpfangsZeit());
        }

        public static MessageDTO withCustomParams(UUID id, String wahlbezirkID, String nachricht, LocalDateTime time) {
            return new MessageDTO(id, wahlbezirkID, nachricht, time);
        }
    }

    public static class CreateBroadcastMessageDto {

        public static BroadcastMessageDTO withCustomParams(List<String> wahlbezirkIds, String nachricht) {
            return new BroadcastMessageDTO(wahlbezirkIds, nachricht);
        }
    }

    public static String asJsonString(final Object obj, ObjectMapper mapper) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
