package de.muenchen.oss.wahllokalsystem.broadcastservice.service;

import de.muenchen.oss.wahllokalsystem.broadcastservice.domain.Message;
import de.muenchen.oss.wahllokalsystem.broadcastservice.rest.BroadcastMessageDTO;
import de.muenchen.oss.wahllokalsystem.broadcastservice.rest.MessageDTO;
import java.time.LocalDateTime;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface BroadcastMapper {

    MessageDTO toDto(Message entityToMap);

    default List<Message> fromBroadcastMessageDTOtoListOfMessages(BroadcastMessageDTO messageToBroadcast, LocalDateTime now) {
        return messageToBroadcast.wahlbezirkIDs().stream().map(wahlbezirkId -> {
            Message message = new Message();
            message.setWahlbezirkID(wahlbezirkId);
            message.setEmpfangsZeit(now);
            message.setNachricht(messageToBroadcast.nachricht());
            return message;
        }).toList();

    }
}
