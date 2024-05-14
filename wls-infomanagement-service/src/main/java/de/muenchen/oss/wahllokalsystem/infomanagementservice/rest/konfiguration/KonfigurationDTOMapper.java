package de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.konfiguration;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.KennbuchstabenListenModel;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.KonfigurationKonfigKey;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.KonfigurationModel;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.KonfigurationSetModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface KonfigurationDTOMapper {

    KonfigurationDTO toDTO(KonfigurationModel konfigurationModel);

    KennbuchstabenListenDTO toDTO(KennbuchstabenListenModel kennbuchstabenListenModel);

    KonfigurationKonfigKey toModelKey(KonfigurationKey konfigurationKey);

    @Mapping(target = "schluessel", source = "konfigurationKey")
    KonfigurationSetModel toSetModel(KonfigurationKey konfigurationKey, KonfigurationSetDTO konfigurationSetDTO);
}
