package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.EingenommenerWahlscheine;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmabgabevermerke;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmzettel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Vermerk;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Wahldaten;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface StimmabgabevermerkeModelMapper {

    StimmabgabevermerkeModel toModel(Stimmabgabevermerke entity);

    Stimmabgabevermerke toEntity(StimmabgabevermerkeModel model);

    @Mapping(target = "id", ignore = true)
    Wahldaten toEntity(WahldatenModel wahldatenModel);

    @Mapping(target = "id", ignore = true)
    Vermerk toEntity(VermerkModel vermerkModel);

    EingenommenerWahlscheine toEntity(EingenommenerWahlscheinModel eingenommenerWahlscheinModel);

    Stimmzettel toEntity(StimmzettelModel stimmzettelModel);
}
