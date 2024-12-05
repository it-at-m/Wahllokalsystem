package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.awerte;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AWerteModel;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface AWerteDTOMapper {

    List<AWerteDTO> fromListOfAWerteModelToListOfAWerteDTO(List<AWerteModel> aWerteModelList);

}
