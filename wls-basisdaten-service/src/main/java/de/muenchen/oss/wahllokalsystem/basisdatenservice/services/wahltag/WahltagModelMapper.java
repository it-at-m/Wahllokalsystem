package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahltag;
import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface WahltagModelMapper {

    @Mapping(target = "id", ignore = true)
    Wahltag toEntity(WahltagModel wahltagModel);

    WahltagModel toModel(Wahltag entity);

    List<WahltagModel> fromWahltagEntityToWahltagModelList(List<Wahltag> entities);

    List<Wahltag> fromWahltagModelToWahltagEntityList(List<WahltagModel> entities);
}
