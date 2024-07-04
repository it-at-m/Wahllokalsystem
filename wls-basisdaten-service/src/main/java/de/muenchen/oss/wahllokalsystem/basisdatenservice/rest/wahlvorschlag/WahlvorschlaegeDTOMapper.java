package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlvorschlag;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlag.WahlvorschlaegeModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WahlvorschlaegeDTOMapper {

    @Mapping(source = "bezirkUndWahlID.wahlID", target = "wahlID")
    @Mapping(source = "bezirkUndWahlID.wahlbezirkID", target = "wahlbezirkID")
    WahlvorschlaegeDTO toDTO(WahlvorschlaegeModel wahlvorschlaegeModel);

}
