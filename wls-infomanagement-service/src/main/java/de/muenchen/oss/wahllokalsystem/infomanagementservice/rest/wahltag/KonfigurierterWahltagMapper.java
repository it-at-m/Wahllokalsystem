package de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.wahltag;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.wahltag.KonfigurierterWahltag;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag.KonfigurierterWahltagModel;
import org.mapstruct.Mapper;

@Mapper
public interface KonfigurierterWahltagMapper {

    KonfigurierterWahltagModel toModel(KonfigurierterWahltag entity);

    KonfigurierterWahltagDTO toDTO(KonfigurierterWahltagModel model);

}
