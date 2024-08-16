package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahlbezirk;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahl;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlbezirkDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface WahlbezirkModelMapper {

    Wahlbezirk fromModeltoEntity(WahlbezirkModel wahlbezirkModel);

    WahlbezirkModel fromEntitytoModel(Wahlbezirk entity);

    @Mapping(source="identifikator", target="wahlbezirkID")
    @Mapping(source="wahlbezirkArt", target="wahlbezirkart")
    Wahlbezirk fromDTOtoEntity(WahlbezirkDTO dto);

    List<Wahlbezirk> fromListOfWahlbezirkModeltoListOfWahlbezirkEntities(List<WahlbezirkModel> wahlbezirkModelList);

    List<WahlbezirkModel> fromListOfWahlbezirkEntityToListOfWahlbezirkModel(List<Wahlbezirk> wahlbezirkEntityList);

    default List<WahlbezirkModel> toWahlbezirkModelListMergedWithWahlenInfo(Set<WahlbezirkModel> remoteWahlbezirke, List<Wahl> wahlen, ExceptionFactory exceptionFactory){
        if(remoteWahlbezirke == null){
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETWAHLBEZIRKE_NO_DATA);
        }
        List<Wahlbezirk> list = new ArrayList<>();
        List<Wahlbezirk> remoteList = this.fromListOfWahlbezirkModeltoListOfWahlbezirkEntities((List<WahlbezirkModel>) remoteWahlbezirke);
        remoteList.forEach(wahlbezirk -> {
            if(null != wahlen){
                Wahl searchedWahl = wahlen.stream().filter(wahl -> wahlbezirk.getWahlnummer().equals(wahl.getNummer())).findFirst().orElse(null);
                if(null != searchedWahl){
                    wahlbezirk.setWahlID(searchedWahl.getWahlID());
                }
            }
            list.add(wahlbezirk);
        });
        return this.fromListOfWahlbezirkEntityToListOfWahlbezirkModel(list);
    }

}
