package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelumschlaege;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelumschlaege.Stimmzettelumschlaege;
import org.mapstruct.Mapper;

@Mapper
public interface StimmzettelumschlaegeModelMapper {

    StimmzettelumschlaegeModel toModel(Stimmzettelumschlaege entity);

    Stimmzettelumschlaege toEntity(StimmzettelumschlaegeModel model);
}
