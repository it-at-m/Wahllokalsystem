package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.wahltag.KonfigurierterWahltag;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface KonfigurierterWahltagModelMapper {

    KonfigurierterWahltagModel toModel(KonfigurierterWahltag entity);

    KonfigurierterWahltag toEntity(KonfigurierterWahltagModel model);

    List<KonfigurierterWahltagModel> toModelList(List<KonfigurierterWahltag> konfigurierteWahltage);
}
