package de.muenchen.oss.wahllokalsystem.service;

import de.muenchen.oss.wahllokalsystem.domain.Message;
import de.muenchen.oss.wahllokalsystem.rest.MessageDTO;
import org.mapstruct.Mapper;

@Mapper
public interface BroadcastMapper {

    MessageDTO toDto(Message entityToMap);
}
