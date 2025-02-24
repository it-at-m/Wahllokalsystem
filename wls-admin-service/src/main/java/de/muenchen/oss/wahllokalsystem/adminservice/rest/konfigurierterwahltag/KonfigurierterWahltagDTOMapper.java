package de.muenchen.oss.wahllokalsystem.adminservice.rest.konfigurierterwahltag;

import de.muenchen.oss.wahllokalsystem.adminservice.service.common.KonfigurierterWahltagModel;
import org.mapstruct.Mapper;

@Mapper
public interface KonfigurierterWahltagDTOMapper {

    KonfigurierterWahltagModel toModel(KonfigurierterWahltagDTO dto);

    KonfigurierterWahltagDTO toDTO(KonfigurierterWahltagModel model);
}
