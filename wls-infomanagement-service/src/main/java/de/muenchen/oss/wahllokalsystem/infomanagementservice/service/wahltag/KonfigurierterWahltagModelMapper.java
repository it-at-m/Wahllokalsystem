package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.wahltag.KonfigurierterWahltag;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface KonfigurierterWahltagModelMapper {

    KonfigurierterWahltagModel toModel(KonfigurierterWahltag entity);

    @Mapping(target = "wahltagStatus", source = "wahltagStatus", defaultValue = "INAKTIV")
    KonfigurierterWahltag toEntity(KonfigurierterWahltagModel model);

    List<KonfigurierterWahltagModel> toModelList(List<KonfigurierterWahltag> konfigurierteWahltage);
}
