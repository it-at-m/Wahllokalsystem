package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.awerte;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AsyncProgress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AsyncProgressDTOMapper {

    @Mapping(target = "aWerteLoadingActive", source = "AWerteLoadingActive")
    @Mapping(target = "aWerteTotal", source = "AWerteTotal")
    @Mapping(target = "aWerteFinished", source = "AWerteFinished")
    @Mapping(target = "aWerteNext", source = "AWerteNext")
    AsyncProgressDTO toDTO(AsyncProgress asyncProgress);
}
