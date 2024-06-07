package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.unterbrechungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UnterbrechungsUhrzeit;
import org.mapstruct.Mapper;

@Mapper
public interface UnterbrechungsUhrzeitModelMapper {

    UnterbrechungsUhrzeitModel toModel(UnterbrechungsUhrzeit entity);

    UnterbrechungsUhrzeit toEntity(UnterbrechungsUhrzeitModel model);
}
