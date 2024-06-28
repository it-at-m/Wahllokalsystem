package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahldaten;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahl;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahltag;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahltagDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WahldatenMapper {

    @Mapping(target = "identifikator", source = "id")
    WahltagDTO toDTO(Wahltag wahltag);

    @Mapping(target = "identifikator", source = "id")
    @Mapping(target = "wahltag", source = "wahltag.tag")
    WahlDTO toDTO(Wahl wahl);
}
