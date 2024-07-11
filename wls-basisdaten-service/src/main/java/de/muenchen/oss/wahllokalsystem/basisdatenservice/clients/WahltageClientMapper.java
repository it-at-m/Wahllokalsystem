package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahltagDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahltageDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag.WahltagModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag.WahltageModel;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WahltageClientMapper {

    @Mapping(target = "wahltagModels", source = "wahltage")
    WahltageModel toWahltageModel(WahltageDTO wahltageDTO);

    @Mapping(target = "wahltagID", source = "identifikator")
    @Mapping(target = "wahltag", source = "tag")
    WahltagModel toWahltagModel(WahltagDTO wahltagDTO);

    default List<WahltagModel> fromRemoteClientWahltageDTOtoListOfWahltagModel(WahltageDTO wahltageDTO) {
        return wahltageDTO.getWahltage().stream().map(this::toWahltagModel).toList();
    }
}
