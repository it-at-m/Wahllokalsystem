package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.clients.basisdaten;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.eai.basisdaten.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.WahlModel;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WahlenClientMapper {

  @Mapping(target = "reihenfolge", constant = "1l")
  WahlModel toModel(WahlDTO wahlDTO);

  List<WahlModel> fromRemoteClientListOfWahlDTOtoListOfWahlModel(List<WahlDTO> wahlDTO);
}
