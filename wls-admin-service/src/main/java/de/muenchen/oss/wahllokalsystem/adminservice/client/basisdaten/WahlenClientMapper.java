package de.muenchen.oss.wahllokalsystem.adminservice.client.basisdaten;

import de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahlen.WahlModel;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WahlenClientMapper {

    @Mapping(target = "nummer", ignore = true)
    WahlDTO toDto(WahlModel wahl);

    List<WahlModel> toModelList(List<WahlDTO> wahlenDTOs);

    List<WahlDTO> toDtoList(List<WahlModel> wahlen);
}
