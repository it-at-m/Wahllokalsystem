package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlbezirk.Wahlbezirk;
import java.util.Collection;
import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface WahlbezirkModelMapper {

    Collection<Wahlbezirk> fromListOfWahlbezirkModeltoListOfWahlbezirkEntities(Collection<WahlbezirkModel> wahlbezirkModelList);

    List<WahlbezirkModel> fromListOfWahlbezirkEntityToListOfWahlbezirkModel(List<Wahlbezirk> wahlbezirkEntityList);

}
