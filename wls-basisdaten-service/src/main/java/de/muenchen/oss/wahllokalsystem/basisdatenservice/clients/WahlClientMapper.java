package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahl.WahlDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahl.WahlModel;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper
public interface WahlClientMapper {
    WahlModel toModel(WahlDTO wahlDTO);

    List<WahlModel> fromRemoteClientSetOfWahlDTOtoListOfWahlModel(Set<WahlDTO> wahlDTO);
}
