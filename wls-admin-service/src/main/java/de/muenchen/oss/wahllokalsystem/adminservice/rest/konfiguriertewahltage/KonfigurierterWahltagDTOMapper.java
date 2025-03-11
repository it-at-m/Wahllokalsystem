package de.muenchen.oss.wahllokalsystem.adminservice.rest.konfiguriertewahltage;

import static de.muenchen.oss.wahllokalsystem.adminservice.rest.konfiguriertewahltage.WahltagStatusDTO.AKTIV;
import static de.muenchen.oss.wahllokalsystem.adminservice.rest.konfiguriertewahltage.WahltagStatusDTO.INAKTIV;

import de.muenchen.oss.wahllokalsystem.adminservice.service.common.KonfigurierterWahltagModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface KonfigurierterWahltagDTOMapper {

    @Mapping(target = "active", source = "wahltagStatusDTO")
    KonfigurierterWahltagModel toModel(KonfigurierterWahltagDTO dto);

    @Mapping(target = "wahltagStatusDTO", source = "active")
    KonfigurierterWahltagDTO toDTO(KonfigurierterWahltagModel model);

    default WahltagStatusDTO mapModelBooleanActiveToWahltagStatusDTOString(boolean active) {
        return active ? AKTIV : INAKTIV;
    }

    default boolean mapWahltagStatusDTOStringToModelBooleanActive(WahltagStatusDTO wahltagStatusDTO) {
        return wahltagStatusDTO == AKTIV;
    }
}
