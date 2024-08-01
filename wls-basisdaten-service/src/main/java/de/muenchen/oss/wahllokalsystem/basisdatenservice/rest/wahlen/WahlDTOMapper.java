package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModel;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WahlDTOMapper {

    WahlDTO toDTO(WahlModel wahlModel);

    WahlModel toModel(WahlDTO wahlDTO);

//    @Mapping(target = "reihenfolge", source="reihenfolge", defaultValue = "0")
//    @Mapping(target = "wahlerverzeichnisnummer", source="wahlerverzeichnisnummer", defaultValue = "0")
    List<WahlDTO> fromListOfWahlModelToListOfWahlDTO(List<WahlModel> wahlen);

    List<WahlModel> fromListOfWahlDTOtoListOfWahlModel(List<WahlDTO> wahlDTOs);
}
