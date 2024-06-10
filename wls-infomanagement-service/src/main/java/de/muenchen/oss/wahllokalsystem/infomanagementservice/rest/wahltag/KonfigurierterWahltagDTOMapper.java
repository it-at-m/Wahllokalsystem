package de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.wahltag;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag.KonfigurierterWahltagModel;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface KonfigurierterWahltagDTOMapper {

    @Mapping(target = "active", source = "wahltagStatus")
    KonfigurierterWahltagModel toModel(KonfigurierterWahltagDTO konfigurierteWahltag);

    @Mapping(target = "wahltagStatus", source = "active")
    KonfigurierterWahltagDTO toDTO(KonfigurierterWahltagModel konfigurierteWahltag);

    List<KonfigurierterWahltagDTO> toDTOList(List<KonfigurierterWahltagModel> konfigurierteWahltage);

    default boolean statusToActiveFlag(final WahltagStatus wahltagStatus) {
        return wahltagStatus == WahltagStatus.AKTIV;
    }

    default WahltagStatus activeFlagToStatus(final boolean activeFlag) {
        return activeFlag ? WahltagStatus.AKTIV : WahltagStatus.INAKTIV;
    }

}
