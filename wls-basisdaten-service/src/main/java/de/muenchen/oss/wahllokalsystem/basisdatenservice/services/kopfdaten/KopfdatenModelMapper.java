package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.Kopfdaten;
import org.mapstruct.Mapper;

@Mapper
public interface KopfdatenModelMapper {

    Kopfdaten toEntity(KopfdatenModel kopfdatenModel);

    KopfdatenModel toModel(Kopfdaten entity);

}
