package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.clients.basisdaten;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.eai.aou.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.FarbeModel;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.WahlModel;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(imports = FarbeModel.class)
public interface WahlenClientMapper {

    @Mapping(target = "waehlerverzeichnisnummer", constant = "1l")
    @Mapping(target = "reihenfolge", constant = "1l")
    @Mapping(target = "farbe", expression = "java(new FarbeModel(0, 0, 0))")
    @Mapping(target = "wahlID", source = "identifikator")
    WahlModel toModel(WahlDTO wahlDTO);

    List<WahlModel> fromRemoteClientSetOfWahlDTOtoListOfWahlModel(Set<WahlDTO> wahlDTO);
}
