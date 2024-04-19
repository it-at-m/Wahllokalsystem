package de.muenchen.oss.wahllokalsystem.broadcastservice.service;

import de.muenchen.oss.wahllokalsystem.broadcastservice.domain.Message;
import de.muenchen.oss.wahllokalsystem.broadcastservice.rest.MessageDTO;
import org.mapstruct.Mapper;

@Mapper
public interface BroadcastMapper {

    MessageDTO toDto(Message entityToMap);
}
