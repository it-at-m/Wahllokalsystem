package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModel;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(imports = Farbe.class)
public interface WahlenClientMapper {

    @Mapping(target = "waehlerverzeichnisnummer", constant = "0l")
    @Mapping(target = "reihenfolge", constant = "0l")
    @Mapping(target = "farbe", expression = "java(new Farbe(0, 0, 0))")
    @Mapping(target = "wahlID", source = "identifikator")
    WahlModel toModel(WahlDTO wahlDTO);

    List<WahlModel> fromRemoteClientSetOfWahlDTOtoListOfWahlModel(Set<WahlDTO> wahlDTO);
}
