package de.muenchen.oss.wahllokalsystem.adminservice.client.infomanagement;

import static de.muenchen.oss.wahllokalsystem.adminservice.eai.infomanagement.model.KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV;
import static de.muenchen.oss.wahllokalsystem.adminservice.eai.infomanagement.model.KonfigurierterWahltagDTO.WahltagStatusEnum.INAKTIV;

import de.muenchen.oss.wahllokalsystem.adminservice.eai.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.KonfigurierterWahltagModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface KonfigurierterWahltagClientMapper {

    @Mapping(target = "wahltagStatus", source = "active")
    KonfigurierterWahltagDTO toDto(KonfigurierterWahltagModel model);

    default KonfigurierterWahltagDTO.WahltagStatusEnum mapModelBooleanActiveToWahltagStatusEnumString(boolean active) {
        return active ? AKTIV : INAKTIV;
    }
}
