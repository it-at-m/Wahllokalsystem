package de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.konfiguration;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.KonfigurationKonfigKey;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.KonfigurationModel;
import org.mapstruct.Mapper;

@Mapper
public interface KonfigurationDTOMapper {

    KonfigurationDTO toDTO(KonfigurationModel konfigurationModel);

    KonfigurationKonfigKey toModelKey(KonfigurationKey konfigurationKey);
}
