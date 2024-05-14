package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.wahltag.KonfigurierterWahltag;
import org.mapstruct.Mapper;

@Mapper
public interface KonfigurierterWahltagModelMapper {

    KonfigurierterWahltagModel toModel(KonfigurierterWahltag entity);

    KonfigurierterWahltag toEntity(KonfigurierterWahltagModel model);
}
