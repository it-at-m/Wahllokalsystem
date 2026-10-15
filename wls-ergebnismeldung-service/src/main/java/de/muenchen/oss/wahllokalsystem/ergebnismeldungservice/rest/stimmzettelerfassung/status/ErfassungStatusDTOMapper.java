package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.status;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.status.ErfassungStatusModel;
import org.mapstruct.Mapper;

@Mapper
public interface ErfassungStatusDTOMapper {

  ErfassungStatusDTO toDTO(ErfassungStatusModel erfassungStatusModel);

  ErfassungStatusModel toModel(ErfassungStatusDTO erfassungStatusDTO);
}
