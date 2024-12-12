package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.EingenommenerWahlschein;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmabgabevermerke;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmzettel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Vermerk;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Wahldaten;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.models.EingenommenerWahlscheinModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.models.StimmabgabevermerkeModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.models.StimmzettelModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.models.VermerkModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.models.WahldatenModel;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface StimmabgabevermerkeModelMapper {

    StimmabgabevermerkeModel toModel(Stimmabgabevermerke entity);

    @Mapping(target = "id", ignore = true)
    Stimmabgabevermerke toEntity(StimmabgabevermerkeModel model);

    @Mapping(target = "stimmabgabevermerke", ignore = true)
    @Mapping(target = "id", ignore = true)
    Wahldaten toEntity(WahldatenModel wahldatenModel);

    @Mapping(target = "wahldaten", ignore = true)
    @Mapping(target = "id", ignore = true)
    Vermerk toEntity(VermerkModel vermerkModel);

    @Mapping(target = "wahldaten", ignore = true)
    @Mapping(target = "id", ignore = true)
    EingenommenerWahlschein toEntity(EingenommenerWahlscheinModel eingenommenerWahlscheinModel);

    @Mapping(target = "vermerk", ignore = true)
    @Mapping(target = "id", ignore = true)
    Stimmzettel toEntity(StimmzettelModel stimmzettelModel);
}
