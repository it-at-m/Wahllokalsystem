package de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.konfiguration;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.konfiguration.dto.KennbuchstabenListenDTO;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.konfiguration.dto.KonfigurationDTO;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.konfiguration.dto.KonfigurationSetDTO;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KennbuchstabenListenModel;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KonfigurationKonfigKey;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KonfigurationModel;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KonfigurationSetModel;
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
