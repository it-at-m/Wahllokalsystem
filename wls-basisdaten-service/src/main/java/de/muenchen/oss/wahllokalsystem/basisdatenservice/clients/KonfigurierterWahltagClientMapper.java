package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.KonfigurierterWahltagModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface KonfigurierterWahltagClientMapper {

    @Mapping(target = "active", source = "wahltagStatus", qualifiedByName = "mapWahltagStatusEnumStringToModelBoolean")
    KonfigurierterWahltagModel fromRemoteClientDTOToModel(KonfigurierterWahltagDTO konfigurierterWahltagDTO);

    @Named("mapWahltagStatusEnumStringToModelBoolean")
    default boolean mapWahltagStatusEnumStringToModelBoolean(KonfigurierterWahltagDTO.WahltagStatusEnum wahltagStatus) {
        if (wahltagStatus.equals(KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV)) {
            return true;
        } else if (wahltagStatus.equals(KonfigurierterWahltagDTO.WahltagStatusEnum.INAKTIV)) {
            return false;
        }
        return false;
    }
}
