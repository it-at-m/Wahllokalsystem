package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModel;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface WahlDTOMapper {

    WahlDTO toDTO(WahlModel wahlModel);

    WahlModel toModel(WahlDTO wahlDTO);

    List<WahlDTO> fromListOfWahlModelToListOfWahlDTO(List<WahlModel> wahlen);

    List<WahlModel> fromListOfWahlDTOtoListOfWahlModel(List<WahlDTO> wahlDTOs);
}
