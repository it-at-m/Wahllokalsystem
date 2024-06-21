package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.eai.model.WahlvorschlaegeDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.eai.model.WahlvorschlagDTO;
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

    // Model to Entity

    default Wahlvorschlaege fromModeltoEntity(WahlvorschlaegeModel wahlvorschlaegeModel) {

        Wahlvorschlaege entity = new Wahlvorschlaege();

        entity.setBezirkUndWahlID(new BezirkUndWahlID(wahlvorschlaegeModel.wahlbezirkID(), wahlvorschlaegeModel.wahlID()));
        entity.setStimmzettelgebietID(wahlvorschlaegeModel.stimmzettelgebietID());
        entity.setWahlvorschlaege(fromSetOfWahlvorschlagModelToSetOfEntity(wahlvorschlaegeModel.wahlvorschlaege()));

        return entity;
    }

    @IterableMapping(qualifiedByName = "fromWahlvorschlagModeltoEntity")
    Set<Wahlvorschlag> fromSetOfWahlvorschlagModelToSetOfEntity(Set<WahlvorschlagModel> wahlvorschlagModels);

    @Named("fromWahlvorschlagModeltoEntity")
    Wahlvorschlag fromClientWahlvorschlagDTOtoEntity(WahlvorschlagModel wahlvorschlagModel);

    // RemoteClientDTO to Entity

    @Mapping(source = "wahlvorschlaege", target = "wahlvorschlaege", qualifiedByName = "fromClientDTOSetToEntitySet")
    Wahlvorschlaege fromClientWahlvorschlaegeDTOtoEntity(WahlvorschlaegeDTO entity);

    @IterableMapping(qualifiedByName = "fromClientWahlvorschlagDTOtoEntity")
    Set<Wahlvorschlag> fromClientDTOSetToEntitySet(Set<WahlvorschlagDTO> wahlvorschlagDTOS);

    @Named("fromClientWahlvorschlagDTOtoEntity")
    Wahlvorschlag fromClientWahlvorschlagDTOtoEntity(WahlvorschlagDTO wahlvorschlagDTO);

    // Entity to Model

    @Mapping(source = "wahlvorschlaege", target = "wahlvorschlaege", qualifiedByName = "toModelSet")
    WahlvorschlaegeModel fromEntityToWahlvorschlaegeModel(Wahlvorschlaege entity);

    @IterableMapping(qualifiedByName = "fromEntityToWahlvorschlagModel")
    Set<WahlvorschlagModel> toModelSet(Set<Wahlvorschlag> entityList);

    @Named("fromEntityToWahlvorschlagModel")
    WahlvorschlagModel fromEntityToWahlvorschlagModel(Wahlvorschlag wahlvorschlagEntity);

}
