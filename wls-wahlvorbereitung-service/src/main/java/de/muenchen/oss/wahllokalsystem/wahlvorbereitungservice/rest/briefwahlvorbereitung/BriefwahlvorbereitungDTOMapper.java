package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.briefwahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.briefwahlvorbereitung.BriefwahlvorbereitungModel;
import org.mapstruct.Mapper;

@Mapper
public interface BriefwahlvorbereitungDTOMapper {

    BriefwahlvorbereitungDTO toDTO(BriefwahlvorbereitungModel model);

    BriefwahlvorbereitungModel toModel(String wahlbezirkID, BriefwahlvorbereitungWriteDTO dto);
}
