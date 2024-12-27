package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.wahlscheine;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.wahlscheine.Wahlscheine;
import org.mapstruct.Mapper;

@Mapper
public interface WahlscheineModelMapper {

    WahlscheineModel toModel(Wahlscheine entity);

    Wahlscheine toEntity(WahlscheineModel model);
}
