package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisseModel;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisseWriteModel;
import org.mapstruct.Mapper;

@Mapper
public interface EreignisDTOMapper {

    WahlbezirkEreignisseDTO toDTO(EreignisseModel model);

    EreignisseWriteModel toModel(String wahlbezirkID, EreignisseWriteDTO dto);
}
