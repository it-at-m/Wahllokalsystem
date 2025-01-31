package de.muenchen.oss.wahllokalsystem.adminservice.client.basisdaten;

import de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.model.WahltagDTO;
import de.muenchen.oss.wahllokalsystem.adminservice.service.WahltagModel;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface WahltagClientMapper {

    WahltagModel toModel(WahltagDTO wahltagDTO);

    List<WahltagModel> fromRemoteClientSetOfWahltagDTOtoListOfWahltagModel(List<WahltagDTO> wahltagDTOs);
}
