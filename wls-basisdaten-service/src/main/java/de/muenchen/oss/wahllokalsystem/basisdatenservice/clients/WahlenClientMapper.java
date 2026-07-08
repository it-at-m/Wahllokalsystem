package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.wahlen.FarbeModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.wahlen.WahlModel;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(imports = FarbeModel.class)
public interface WahlenClientMapper {

  @Mapping(target = "waehlerverzeichnisNummer", constant = "1l")
  @Mapping(target = "reihenfolge", constant = "1l")
  @Mapping(target = "farbe", expression = "java(new FarbeModel(0, 0, 0))")
  @Mapping(target = "wahlID", source = "identifikator")
  @Mapping(
      target = "kennzeichen",
      expression = "java(wahlDTO.getWahlart().name().substring(0, 1).toUpperCase())")
  WahlModel toModel(WahlDTO wahlDTO);

  List<WahlModel> fromRemoteClientSetOfWahlDTOtoListOfWahlModel(Set<WahlDTO> wahlDTO);
}
