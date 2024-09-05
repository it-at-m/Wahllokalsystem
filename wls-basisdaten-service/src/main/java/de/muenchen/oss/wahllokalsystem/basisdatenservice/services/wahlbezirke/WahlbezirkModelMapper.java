package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahlbezirk;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlbezirkDTO;
import java.util.Collection;
import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface WahlbezirkModelMapper {

    @Mapping(source = "identifikator", target = "wahlbezirkID")
    @Mapping(source = "wahlbezirkArt", target = "wahlbezirkart")
    Wahlbezirk fromDTOtoEntity(WahlbezirkDTO dto);

    Collection<Wahlbezirk> fromListOfWahlbezirkModeltoListOfWahlbezirkEntities(Collection<WahlbezirkModel> wahlbezirkModelList);

    List<WahlbezirkModel> fromListOfWahlbezirkEntityToListOfWahlbezirkModel(List<Wahlbezirk> wahlbezirkEntityList);

}
