package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.BasisdatenDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.BasisdatenModel;
import org.mapstruct.Mapper;

@Mapper(uses = { WahlbezirkeClientMapper.class, WahlenClientMapper.class })
public interface WahldatenClientMapper {

    BasisdatenModel fromRemoteClientDTOToModel(BasisdatenDTO basisdatenDTO);
}
