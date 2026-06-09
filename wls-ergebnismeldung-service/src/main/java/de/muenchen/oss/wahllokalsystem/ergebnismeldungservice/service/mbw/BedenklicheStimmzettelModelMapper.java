package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw.BedenklicheStimmzettel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw.BedenklicherStimmzettel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Collection;
import lombok.val;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface BedenklicheStimmzettelModelMapper {

  default BedenklicheStimmzettel toEntity(
      Collection<BedenklicherStimmzettelModel> bedenklicherStimmzettelModel,
      String wahlbezirkID,
      String wahlID) {
    val result = new BedenklicheStimmzettel();
    result.setCompositeId(new BezirkUndWahlID(wahlID, wahlbezirkID));

    bedenklicherStimmzettelModel.forEach(
        bedenklicherStimmzettel -> {
          result.addBedenklicheStimmzettels(
              toEntity(bedenklicherStimmzettel, wahlbezirkID, wahlID));
        });

    return result;
  }

  @Mapping(target = "compositeId.wahlbezirkID", source = "wahlbezirkID")
  @Mapping(target = "compositeId.wahlID", source = "wahlID")
  @Mapping(target = "compositeId.orderIndex", source = "bedenklicherStimmzettelModel.orderIndex")
  @Mapping(target = "erfassung", ignore = true)
  BedenklicherStimmzettel toEntity(
      BedenklicherStimmzettelModel bedenklicherStimmzettelModel,
      String wahlbezirkID,
      String wahlID);

  Collection<BedenklicherStimmzettelModel> toModel(
      Collection<BedenklicherStimmzettel> collectionOfModels);

  @Mapping(target = "orderIndex", source = "compositeId.orderIndex")
  BedenklicherStimmzettelModel toModel(BedenklicherStimmzettel entity);
}
