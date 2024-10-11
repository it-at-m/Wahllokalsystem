package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.clients.basisdaten;


import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.eai.aou.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.WahlModel;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.model.Farbe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(imports = Farbe.class)
public interface WahlenClientMapper {

    @Mapping(target = "waehlerverzeichnisnummer", constant = "1l")
    @Mapping(target = "reihenfolge", constant = "1l")
    @Mapping(target = "farbe", expression = "java(new Farbe(0, 0, 0))")
    @Mapping(target = "wahlID", source = "identifikator")
    WahlModel toModel(WahlDTO wahlDTO);

    List<WahlModel> fromRemoteClientSetOfWahlDTOtoListOfWahlModel(Set<WahlDTO> wahlDTO);
}
