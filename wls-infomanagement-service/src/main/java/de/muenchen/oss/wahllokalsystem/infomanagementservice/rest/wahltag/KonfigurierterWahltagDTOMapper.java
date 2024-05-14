package de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.wahltag;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag.KonfigurierterWahltagModel;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface KonfigurierterWahltagDTOMapper {

    KonfigurierterWahltagModel toModel(KonfigurierterWahltagDTO konfigurierteWahltag);

    KonfigurierterWahltagDTO toDTO(KonfigurierterWahltagModel konfigurierteWahltag);

    List<KonfigurierterWahltagDTO> toDTOList(List<KonfigurierterWahltagModel> konfigurierteWahltage);

}
