package de.muenchen.oss.wahllokalsystem.adminservice.rest.wahlen;

import de.muenchen.oss.wahllokalsystem.adminservice.service.wahlen.WahlModel;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface WahlDTOMapper {

    WahlDTO toDto(WahlModel wahlModel);

    WahlModel toModel(WahlDTO wahlDTO);

    List<WahlDTO> toDtoList(List<WahlModel> wahlen);

    List<WahlModel> toModelList(List<WahlDTO> wahlDTOs);
}
