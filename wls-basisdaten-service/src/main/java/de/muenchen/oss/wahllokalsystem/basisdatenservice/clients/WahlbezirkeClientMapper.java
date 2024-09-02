package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlbezirkDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke.WahlbezirkModel;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WahlbezirkeClientMapper {

    @Mapping(source = "identifikator", target = "wahlbezirkID")
    @Mapping(source = "wahlbezirkArt", target = "wahlbezirkart")
    WahlbezirkModel fromClientDTOToModel(WahlbezirkDTO clientDTO);

    Set<WahlbezirkModel> fromRemoteSetOfDTOsToSetOfModels(Set<WahlbezirkDTO> wahlbezirkDTOs);

}
