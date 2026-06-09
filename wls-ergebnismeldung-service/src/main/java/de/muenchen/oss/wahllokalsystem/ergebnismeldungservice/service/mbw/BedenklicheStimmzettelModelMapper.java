package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw.BedenklicheStimmzettel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BedenklicheStimmzettelModelMapper {

  @Mapping(target = "compositeId.wahlbezirkID", source = "wahlbezirkID")
  @Mapping(target = "compositeId.wahlID", source = "wahlID")
  @Mapping(target = "compositeId.orderIndex", source = "bedenklicheStimmzettelModel.orderIndex")
  BedenklicheStimmzettel toEntity(
      BedenklicheStimmzettelModel bedenklicheStimmzettelModel, String wahlbezirkID, String wahlID);

  @Mapping(target = "orderIndex", source = "compositeId.orderIndex")
  BedenklicheStimmzettelModel toModel(BedenklicheStimmzettel entity);
}
