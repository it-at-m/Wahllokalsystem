package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahl;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahl;
import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface WahlModelMapper {

    List<Wahl> toEntity(List <WahlModel> wahlModel);

    List <WahlModel> toModel(List <Wahl> entity);

    Wahl toEntity (WahlModel wahlModel);

    Wahl toModel (Wahl entity);

}
