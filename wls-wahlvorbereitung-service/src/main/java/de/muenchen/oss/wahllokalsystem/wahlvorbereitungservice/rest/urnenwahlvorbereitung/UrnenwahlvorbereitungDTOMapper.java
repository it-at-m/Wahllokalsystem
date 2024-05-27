package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.urnenwahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.common.WahlurneDTOMapper;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlvorbereitung.UrnenwahlvorbereitungModel;
import org.mapstruct.Mapper;

@Mapper(uses = WahlurneDTOMapper.class)
public interface UrnenwahlvorbereitungDTOMapper {

    UrnenwahlvorbereitungDTO toDTO(UrnenwahlvorbereitungModel model);
}
