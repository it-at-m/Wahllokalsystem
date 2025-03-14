package de.muenchen.oss.wahllokalsystem.broadcastservice.rest;

import de.muenchen.oss.wahllokalsystem.broadcastservice.domain.Message;
import java.time.LocalDateTime;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface BroadcastDTOMapper {

    MessageDTO toDto(Message entityToMap);

    default List<Message> toListOfMessageEntity(BroadcastMessageDTO messageToBroadcast, LocalDateTime now) {
        return messageToBroadcast.wahlbezirkIDs().stream().map(wahlbezirkId -> {
            Message message = new Message();
            message.setWahlbezirkID(wahlbezirkId);
            message.setEmpfangsZeit(now);
            message.setNachricht(messageToBroadcast.nachricht());
            return message;
        }).toList();
    }
}
