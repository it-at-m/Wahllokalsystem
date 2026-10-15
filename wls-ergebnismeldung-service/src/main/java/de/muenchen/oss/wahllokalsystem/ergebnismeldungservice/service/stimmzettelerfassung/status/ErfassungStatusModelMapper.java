package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.status;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.status.ErfassungStatus;
import org.mapstruct.Mapper;

@Mapper
public interface ErfassungStatusModelMapper {

  ErfassungStatusModel toModel(ErfassungStatus erfassungStatus);

  ErfassungStatus toEntity(ErfassungStatusModel erfassungStatusModel);
}
