package de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.wahltag;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag.KonfigurierterWahltagModel;
import org.mapstruct.Mapper;

@Mapper
public interface KonfigurierterWahltagDTOMapper {

    KonfigurierterWahltagModel toModel(KonfigurierterWahltagDTO dto);

    KonfigurierterWahltagDTO toDTO(KonfigurierterWahltagModel model);

}
