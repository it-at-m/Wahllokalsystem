package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.status.Status;
import org.mapstruct.Mapper;

@Mapper
public interface StatusModelMapper {

    StatusModel toModel(Status entity);

    Status toEntity(StatusModel model);
}
