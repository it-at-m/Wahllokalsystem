package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.eai.model.KandidatDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.eai.model.WahlvorschlaegeDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.eai.model.WahlvorschlagDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Kandidat;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahlvorschlaege;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahlvorschlag;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Set;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface WahlvorschlaegeModelMapper {

    // RemoteClientDTO to Entity

    @Mapping(source="wahlbezirkID", target="bezirkUndWahlID.wahlbezirkID")
    @Mapping(source="wahlID", target="bezirkUndWahlID.wahlID")
    @Mapping(source = "wahlvorschlaege", target = "wahlvorschlaege", qualifiedByName = "fromClientDTOSetToEntitySet")
    Wahlvorschlaege fromClientWahlvorschlaegeDTOtoEntity(WahlvorschlaegeDTO entity);

    @IterableMapping(qualifiedByName = "fromClientWahlvorschlagDTOtoEntity")
    @Named("fromClientDTOSetToEntitySet")
    Set<Wahlvorschlag> fromClientDTOSetToEntitySet(Set<WahlvorschlagDTO> wahlvorschlagDTOS);

    @Named("fromClientWahlvorschlagDTOtoEntity")
    Wahlvorschlag fromClientWahlvorschlagDTOtoEntity(WahlvorschlagDTO wahlvorschlagDTO);

    Kandidat fromKandidatDTOToKandidatEntity(KandidatDTO kandidatDTO);

    // Entity to Model
    @Mapping(source="bezirkUndWahlID.wahlbezirkID", target="wahlbezirkID")
    @Mapping(source="bezirkUndWahlID.wahlID", target="wahlID")
    @Mapping(source = "wahlvorschlaege", target = "wahlvorschlaege", qualifiedByName = "fromSetOfWahlvorschlagEntityToSetOfModel")
    WahlvorschlaegeModel fromEntityToWahlvorschlaegeModel(Wahlvorschlaege entity);

    @IterableMapping(qualifiedByName = "fromEntityToWahlvorschlagModel")
    @Named("fromSetOfWahlvorschlagEntityToSetOfModel")
    Set<WahlvorschlagModel> fromSetOfWahlvorschlagEntityToSetOfModel(Set<Wahlvorschlag> entityList);

    @Named("fromEntityToWahlvorschlagModel")
    WahlvorschlagModel fromEntityToWahlvorschlagModel(Wahlvorschlag wahlvorschlagEntity);

    KandidatModel fromKandidatToKandidatModel(Kandidat kandidat);

}
