package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignis;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisse;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface EreignisseModelMapper {

  @Mapping(target = "ereignisse", source = "ereigniseintraege")
  Ereignisse toEntity(EreignisseModel ereignisseModel);

  @Mapping(target = "ereigniseintraege", source = "ereignisse")
  EreignisseModel toModel(final Ereignisse ereignisseEntity);

  Ereignis toEntity(final EreignisModel ereignisModel);

  EreignisModel toModel(final Ereignis ereignisEntity);
}
