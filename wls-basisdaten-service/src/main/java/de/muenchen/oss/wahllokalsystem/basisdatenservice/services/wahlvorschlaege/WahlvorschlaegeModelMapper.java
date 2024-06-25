package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.eai.model.KandidatDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.eai.model.WahlvorschlaegeDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.eai.model.WahlvorschlagDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Kandidat;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahlvorschlaege;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahlvorschlag;
import java.util.Set;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface WahlvorschlaegeModelMapper {

    // RemoteClientDTO to Entity

    @Mapping(source = "wahlbezirkID", target = "bezirkUndWahlID.wahlbezirkID")
    @Mapping(source = "wahlID", target = "bezirkUndWahlID.wahlID")
    Wahlvorschlaege fromClientWahlvorschlaegeDTOtoEntity(WahlvorschlaegeDTO entity);

    @Mapping(target = "wahlvorschlaeage", ignore = true)
    Wahlvorschlag fromClientWahlvorschlagDTOtoEntity(WahlvorschlagDTO wahlvorschlagDTO);

    @Mapping(target = "wahlvorschlag", ignore = true)
    Kandidat fromKandidatDTOToKandidatEntity(KandidatDTO kandidatDTO);

    // Entity to Model
    @Mapping(source = "bezirkUndWahlID.wahlbezirkID", target = "wahlbezirkID")
    @Mapping(source = "bezirkUndWahlID.wahlID", target = "wahlID")
    @Mapping(source = "wahlvorschlaege", target = "wahlvorschlaege", qualifiedByName = "fromSetOfWahlvorschlagEntityToSetOfModel")
    WahlvorschlaegeModel fromEntityToWahlvorschlaegeModel(Wahlvorschlaege entity);

    @IterableMapping(qualifiedByName = "fromEntityToWahlvorschlagModel")
    @Named("fromSetOfWahlvorschlagEntityToSetOfModel")
    Set<WahlvorschlagModel> fromSetOfWahlvorschlagEntityToSetOfModel(Set<Wahlvorschlag> entityList);

    @Named("fromEntityToWahlvorschlagModel")
    WahlvorschlagModel fromEntityToWahlvorschlagModel(Wahlvorschlag wahlvorschlagEntity);

    KandidatModel fromKandidatToKandidatModel(Kandidat kandidat);

}
