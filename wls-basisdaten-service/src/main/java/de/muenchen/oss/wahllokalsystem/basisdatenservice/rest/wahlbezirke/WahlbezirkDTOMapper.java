package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlbezirke;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke.WahlbezirkModel;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface WahlbezirkDTOMapper {
    List<WahlbezirkDTO> fromListOfWahlbezirkModelToListOfWahlbezirkDTO(List<WahlbezirkModel> wahlbezirkModelList);
}
