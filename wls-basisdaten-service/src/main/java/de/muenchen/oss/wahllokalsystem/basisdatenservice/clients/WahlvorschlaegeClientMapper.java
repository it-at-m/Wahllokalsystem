package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlvorschlaegeDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlag.WahlvorschlaegeModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WahlvorschlaegeClientMapper {

    @Mapping(target = "bezirkUndWahlID.wahlID", source = "wahlID")
    @Mapping(target = "bezirkUndWahlID.wahlbezirkID", source = "wahlbezirkID")
    WahlvorschlaegeModel toModel(WahlvorschlaegeDTO wahlvorschlaegeDTO);
}
