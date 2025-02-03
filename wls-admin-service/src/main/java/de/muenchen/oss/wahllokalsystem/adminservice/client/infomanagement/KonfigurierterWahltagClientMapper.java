package de.muenchen.oss.wahllokalsystem.adminservice.client.infomanagement;

import de.muenchen.oss.wahllokalsystem.adminservice.eai.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.adminservice.service.KonfigurierterWahltagModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface KonfigurierterWahltagClientMapper {

    @Mapping(target = "wahltagStatus", source = "active")
    KonfigurierterWahltagDTO toClientDto(KonfigurierterWahltagModel model);
}
