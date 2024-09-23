package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisseWriteModel;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.WahlbezirkEreignisseModel;
import org.mapstruct.Mapper;

@Mapper
public interface EreignisDTOMapper {

    WahlbezirkEreignisseDTO toDTO(WahlbezirkEreignisseModel model);

    EreignisseWriteModel toModel(String wahlbezirkID, EreignisseWriteDTO dto);
}
