package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.Kopfdaten;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface KopfdatenModelMapper {

    Kopfdaten toEntity(KopfdatenModel kopfdatenModel);

    KopfdatenModel toModel(Kopfdaten entity);

    List<KopfdatenModel> fromListOfEntitiesToListOfModels(List<Kopfdaten> kopfdatenEntitiesList);

}
