package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisModel;
import org.mapstruct.Mapper;

@Mapper
public interface EreignisDTOMapper {

    EreignisDTO toDTO(EreignisModel model);

    EreignisModel toModel(String wahlbezirkID, EreignisWriteDTO dto);
}
