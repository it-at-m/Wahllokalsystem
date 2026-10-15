package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common.DokumentartDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.DokumentartModel;
import org.mapstruct.Mapper;

@Mapper
public interface DokumentartDTOMapper {

  DokumentartModel toModel(DokumentartDTO dokumentartDTO);
}
