package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlbeteiligung;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlbeteiligung.Wahlbeteiligung;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlbeteiligung.dto.WahlbeteiligungsMeldungDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WahlbeteiligungMapper {

    WahlbeteiligungsMeldungDTO toDTO(Wahlbeteiligung entity);

    @Mapping(target = "id", ignore = true)
    Wahlbeteiligung toEntity(WahlbeteiligungsMeldungDTO dto);

}
