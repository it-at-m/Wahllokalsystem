package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.StapelartModel;
import org.mapstruct.Mapper;

@Mapper
public interface StapelartDTOMapper {

  StapelartDTO toDTO(StapelartModel stapelartModel);

  StapelartModel toModel(StapelartDTO stapelartDTO);
}
