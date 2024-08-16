package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlbezirkDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke.WahlbezirkModel;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper
public interface WahlbezirkeClientMapper {
    Set<WahlbezirkModel> fromRemoteSetOfDTOsToSetOfModels(Set<WahlbezirkDTO> wahlbezirkDTOs);
}
