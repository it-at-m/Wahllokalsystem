package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahltag;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.wahltag.WahltagModel;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface WahltageDTOMapper {
  List<WahltagDTO> fromListOfWahltagModelToListOfWahltagDTO(List<WahltagModel> wahltage);
}
