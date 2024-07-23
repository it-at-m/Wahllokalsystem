package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.Kopfdaten;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface KopfdatenModelMapper {

    @Mapping(target = "id", ignore = true)
    Kopfdaten toEntity(KopfdatenModel kopfdatenModel);

    KopfdatenModel toModel(Kopfdaten entity);

}
