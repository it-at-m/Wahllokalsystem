package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw.BedenklicheStimmzettel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw.BedenklicherStimmzettel;
import java.util.Collection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BedenklicheStimmzettelModelMapper {

  @Mapping(target = "compositeId.wahlbezirkID", source = "wahlbezirkID")
  @Mapping(target = "compositeId.wahlID", source = "wahlID")
  @Mapping(target = "bedenklicheStimmzettel", source = "bedenklicherStimmzettelModel")
  BedenklicheStimmzettel toEntity(
      Collection<BedenklicherStimmzettelModel> bedenklicherStimmzettelModel,
      String wahlbezirkID,
      String wahlID);

  BedenklicherStimmzettel toEntity(BedenklicherStimmzettelModel bedenklicherStimmzettelModel);

  Collection<BedenklicherStimmzettelModel> toModel(
      Collection<BedenklicherStimmzettel> collectionOfModels);

  BedenklicherStimmzettelModel toModel(BedenklicherStimmzettel entity);
}
