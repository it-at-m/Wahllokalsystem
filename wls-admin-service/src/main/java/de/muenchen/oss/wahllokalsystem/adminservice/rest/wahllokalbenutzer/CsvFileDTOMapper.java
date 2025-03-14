package de.muenchen.oss.wahllokalsystem.adminservice.rest.wahllokalbenutzer;

import de.muenchen.oss.wahllokalsystem.adminservice.service.wahllokalbenutzer.CsvFileModel;
import org.mapstruct.Mapper;

@Mapper
public interface CsvFileDTOMapper {
    CsvFileDTO toDTO(CsvFileModel csvFileModel);
}
