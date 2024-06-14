package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.eroeffnungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.EroeffnungsUhrzeit;
import org.mapstruct.Mapper;

@Mapper
public interface EroeffnungsUhrzeitModelMapper {

    EroeffnungsUhrzeitModel toModel(EroeffnungsUhrzeit entity);

    EroeffnungsUhrzeit toEntity(EroeffnungsUhrzeitModel model);
}
