package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WLSWahlvorschlaege;
import org.mapstruct.Mapper;

@Mapper
public interface WahlvorschlaegeModelMapper {

    WahlvorschlaegeModel toModel(WLSWahlvorschlaege entity);

    WLSWahlvorschlaege toEntity(WahlvorschlaegeModel model);
}
