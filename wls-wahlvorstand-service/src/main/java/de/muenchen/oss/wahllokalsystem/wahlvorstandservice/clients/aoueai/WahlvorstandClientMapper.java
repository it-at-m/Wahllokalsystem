package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.clients.aoueai;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.eai.aou.model.WahlvorstandDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.eai.aou.model.WahlvorstandsaktualisierungDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.eai.aou.model.WahlvorstandsmitgliedDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.WahlvorstandsmitgliedModel;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.aoueaiClient.WahlvorstandModel;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WahlvorstandClientMapper {

    ZoneId ZONE_ID = ZoneId.systemDefault();

    @Mapping(target = "anwesenheitBeginn", ignore = true) // todo: darf das ignoriert werden?
    @Mapping(target = "wahlvorstandsmitglieder", source = "mitglieder")
    WahlvorstandModel toModel(WahlvorstandDTO wahlvorstandDTO);

    @Mapping(target = "familienname", source = "nachname")
    @Mapping(target = "funktionsname", ignore = true) // todo: darf das ignoriert werden?
    WahlvorstandsmitgliedModel toModel(WahlvorstandsmitgliedDTO wahlvorstandsmitgliedDTO);

    @Mapping(target = "mitglieder", source = "wahlvorstandsmitglieder")
    WahlvorstandsaktualisierungDTO toWahlvorstandsaktualisierungDTO(WahlvorstandModel wahlvorstandModel);

    default OffsetDateTime toOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZONE_ID).toOffsetDateTime();
    }
}
