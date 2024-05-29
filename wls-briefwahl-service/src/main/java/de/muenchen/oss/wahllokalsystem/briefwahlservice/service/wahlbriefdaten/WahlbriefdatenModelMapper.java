package de.muenchen.oss.wahllokalsystem.briefwahlservice.service.wahlbriefdaten;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.domain.Wahlbriefdaten;
import org.mapstruct.Mapper;

@Mapper
public interface WahlbriefdatenModelMapper {

    WahlbriefdatenModel toModel(final Wahlbriefdaten entity);

    Wahlbriefdaten toEntity(final WahlbriefdatenModel model);
}
