package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.ereignis.EreignisseModel;
import org.mapstruct.Mapper;

@Mapper
public interface EreignisDTOMapper {

  WahlbezirkEreignisseDTO toDTO(EreignisseModel model);

  EreignisseModel toModel(String wahlbezirkID, EreignisseWriteDTO dto);
}
