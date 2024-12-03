package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerte;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface AWerteModelMapper {

    List<AWerte> fromListOfAWerteModeltoListOfAWerteEntity(List<AWerteModel> aWerteModelList);

    List<AWerteModel> fromListOfAWerteEntityToListOfAWerteModel(List<AWerte> aWerteEntityList);
}
