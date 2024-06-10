package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.common;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.common.WahlurneModel;
import org.mapstruct.Mapper;

@Mapper
public interface WahlurneDTOMapper {

    WahlurneDTO toDTO(WahlurneModel wahlurneModel);
}
