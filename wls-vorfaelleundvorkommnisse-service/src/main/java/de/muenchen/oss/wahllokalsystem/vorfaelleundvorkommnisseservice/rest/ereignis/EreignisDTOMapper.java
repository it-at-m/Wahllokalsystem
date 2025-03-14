package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.ereignis.EreignisseWriteModel;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.ereignis.WahlbezirkEreignisseModel;
import org.mapstruct.Mapper;

@Mapper
public interface EreignisDTOMapper {

    WahlbezirkEreignisseDTO toDTO(WahlbezirkEreignisseModel model);

    EreignisseWriteModel toModel(String wahlbezirkID, EreignisseWriteDTO dto);
}
