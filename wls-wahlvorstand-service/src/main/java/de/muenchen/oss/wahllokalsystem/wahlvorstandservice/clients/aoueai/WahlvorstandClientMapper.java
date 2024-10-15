package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.clients.aoueai;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.eai.aou.model.WahlvorstandDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.eai.aou.model.WahlvorstandsmitgliedDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.WahlvorstandsmitgliedModel;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.aoueaiClient.WahlvorstandModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WahlvorstandClientMapper {

    @Mapping(target = "anwesenheitBeginn", ignore = true) // todo: darf das ignoriert werden?
    @Mapping(target = "wahlvorstandsmitglieder", source = "mitglieder")
    WahlvorstandModel toModel(WahlvorstandDTO wahlvorstandDTO);

    @Mapping(target = "familienname", source = "nachname")
    @Mapping(target = "funktionsname", ignore = true) // todo: darf das ignoriert werden?
    WahlvorstandsmitgliedModel toModel(WahlvorstandsmitgliedDTO wahlvorstandsmitgliedDTO);
}
