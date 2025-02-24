package de.muenchen.oss.wahllokalsystem.adminservice.rest.wahltage;

import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahltagModel;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface WahltagDTOMapper {

    WahltagDTO toDto(WahltagModel wahltagModel);

    WahltagModel toModel(WahltagDTO wahltagDTO);

    List<WahltagDTO> toDtoList(List<WahltagModel> wahltagModels);

    List<WahltagModel> toModelList(List<WahltagDTO> wahltagDTOS);
}
