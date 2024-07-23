package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahl;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahl.WahlModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WahlDTOMapper {

    @Mapping(source = "bezirkUndWahlID.wahlID", target = "wahlID")
    @Mapping(source = "bezirkUndWahlID.wahlbezirkID", target = "wahlbezirkID")
    WahlDTO toDTO(WahlModel wahlModel);

}
