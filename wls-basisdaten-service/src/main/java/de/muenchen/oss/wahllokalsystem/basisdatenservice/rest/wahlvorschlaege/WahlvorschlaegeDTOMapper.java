package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlvorschlaege;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege.WahlvorschlaegeModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege.WahlvorschlagModel;
import java.util.Set;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface WahlvorschlaegeDTOMapper {

    @Mapping(source = "wahlvorschlaege", target = "wahlvorschlaege", qualifiedByName = "toWLSDTOSet")
    WahlvorschlaegeDTO fromWahlvorschlaegeModelToWLSDTO(WahlvorschlaegeModel wahlvorschlaegeModel);

    @IterableMapping(qualifiedByName = "fromWahlvorschlagModelToWLSDTO")
    Set<WahlvorschlagDTO> toWLSDTOSet(Set<WahlvorschlagModel> wahlvorschlagModels);

    @Named("fromWahlvorschlagModelToWLSDTO")
    WahlvorschlagDTO fromWahlvorschlagModelToWLSDTO(WahlvorschlagModel wahlvorschlagModel);
}
