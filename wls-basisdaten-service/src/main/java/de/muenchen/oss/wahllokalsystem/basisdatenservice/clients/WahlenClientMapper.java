package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModel;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WahlenClientMapper {

    @Mapping(target = "reihenfolge", ignore = true)
    @Mapping(target = "waehlerverzeichnisnummer", ignore = true)
    @Mapping(target = "farbe", ignore = true)
    @Mapping(target = "wahlID", source="identifikator")
    WahlModel toModel(WahlDTO wahlDTO);

    @Mapping(target = "reihenfolge", defaultValue = "0")
    @Mapping(target = "wahlerverzeichnisnummer", defaultValue = "0")
    List<WahlModel> fromRemoteClientSetOfWahlDTOtoListOfWahlModel(Set<WahlDTO> wahlDTO);
}
