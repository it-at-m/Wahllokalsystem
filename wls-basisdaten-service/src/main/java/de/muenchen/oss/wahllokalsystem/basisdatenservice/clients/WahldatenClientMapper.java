package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.BasisdatenDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.BasisdatenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WahldatenClientMapper {

    BasisdatenModel fromRemoteClientDTOToModel(BasisdatenDTO basisdatenDTO);

    default WahlModel wahlDTOToWahlModel(WahlDTO wahlDTO) {
        return Mappers.getMapper(WahlenClientMapper.class).toModel(wahlDTO);
    }
}
