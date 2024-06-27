package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.eai.model.WahlvorschlaegeDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege.WahlvorschlaegeModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WahlvorschlaegeClientMapper {

    @Mapping(target = "bezirkUndWahlID.wahlID", source = "wahlID")
    @Mapping(target = "bezirkUndWahlID.wahlbezirkID", source = "wahlbezirkID")
    WahlvorschlaegeModel toModel(WahlvorschlaegeDTO wahlvorschlaegeDTO);
}
