package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahl;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlDTO;
import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface WahlModelMapper {

    List<Wahl> toEntity(List <WahlModel> wahlModel);

    List <WahlModel> toModel(List <Wahl> entity);

    Wahl toEntity (WahlModel wahlModel);

    Wahl toModel (Wahl entity);

    List<Wahl> fromListOfRemoteWahlDTOtoListOfWahlEntities(List <WahlDTO> wahlDTOList);

    List<Wahl> fromListOfWahlModeltoListOfWahlEntities(List <WahlModel> wahlModelList);

    List<WahlModel> fromListOfWahlEntityToListOfWahlModel(List <Wahl> wahlEntityList);

}
