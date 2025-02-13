package de.muenchen.oss.wahllokalsystem.adminservice.client.basisdaten;

import de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.model.WahlbezirkDTO;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.WahlbezirkModel;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface WahlbezirkeClientMapper {

    List<WahlbezirkModel> fromListOfWahlbezirkDTOtoListOfWahlbezirkModel(List<WahlbezirkDTO> wahlbezirkeDTOs);
}
