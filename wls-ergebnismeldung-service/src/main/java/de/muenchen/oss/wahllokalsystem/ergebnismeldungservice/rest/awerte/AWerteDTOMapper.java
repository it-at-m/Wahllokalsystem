package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.awerte;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AWerteModel;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AWerteDTOMapper {

    @Mapping(source = "bezirkUndWahlID.wahlID", target = "wahlID")
    @Mapping(source = "bezirkUndWahlID.wahlbezirkID", target = "wahlbezirkID")
    AWerteDTO toDTO(AWerteModel aWerteModel);

    List<AWerteDTO> fromListOfAWerteModelToListOfAWerteDTO(List<AWerteModel> aWerteModelList);
}
