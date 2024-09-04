package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.KonfigurierterWahltagModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface KonfigurierterWahltagClientMapper {

    @Mapping(target = "active", source = "wahltagStatus")
    KonfigurierterWahltagModel fromRemoteClientDTOToModel(KonfigurierterWahltagDTO konfigurierterWahltagDTO);

    default boolean mapWahltagStatusEnumStringToModelBoolean(KonfigurierterWahltagDTO.WahltagStatusEnum wahltagStatus) {
        return switch (wahltagStatus) {
        case AKTIV -> true;
        case INAKTIV -> false;
        };
    }
}
