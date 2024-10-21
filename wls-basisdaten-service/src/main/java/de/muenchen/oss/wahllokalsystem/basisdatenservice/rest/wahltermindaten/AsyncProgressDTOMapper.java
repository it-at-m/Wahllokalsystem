package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahltermindaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltermindaten.AsyncProgress;
import org.mapstruct.Mapper;

@Mapper
public interface AsyncProgressDTOMapper {

    AsyncProgressDTO toDto(AsyncProgress asyncProgress);
}
