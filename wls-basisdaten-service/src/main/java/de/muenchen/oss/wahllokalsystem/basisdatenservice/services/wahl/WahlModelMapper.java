package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahl;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahl;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface WahlModelMapper {

    @Mapping(target = "wahl", ignore = true)
    @Mapping(target = "id", ignore = true)
    Wahl toEntity(WahlModel wahlModel);

    WahlModel toModel(Wahl entity);

}
