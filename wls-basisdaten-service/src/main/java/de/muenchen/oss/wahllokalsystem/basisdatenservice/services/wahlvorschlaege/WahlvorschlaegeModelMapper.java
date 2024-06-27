package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Kandidat;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahlvorschlaege;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahlvorschlag;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface WahlvorschlaegeModelMapper {

    @Mapping(target = "id", ignore = true)
    Wahlvorschlaege toEntity(WahlvorschlaegeModel wahlvorschlaegeModel);

    @Mapping(target = "wahlvorschlaeage", ignore = true)
    @Mapping(target = "id", ignore = true)
    Wahlvorschlag toEntity(WahlvorschlagModel wahlvorschlagModel);

    @Mapping(target = "wahlvorschlag", ignore = true)
    @Mapping(target = "id", ignore = true)
    Kandidat toEntity(KandidatModel kandidatModel);

    WahlvorschlaegeModel toModel(Wahlvorschlaege entity);

}
