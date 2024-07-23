package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahltagDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag.WahltagModel;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WahltageClientMapper {

    @Mapping(target = "wahltagID", source = "identifikator")
    @Mapping(target = "wahltag", source = "tag")
    WahltagModel toWahltagModel(WahltagDTO wahltagDTO);

    List<WahltagModel> fromRemoteClientSetOfWahltagDTOtoListOfWahltagModel(Set<WahltagDTO> wahltageDTO);
}
