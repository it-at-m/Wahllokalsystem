package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.waehlerverzeichnis;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.Waehlerverzeichnis;
import org.mapstruct.Mapper;

@Mapper
public interface WaehlerverzeichnisModelMapper {

    Waehlerverzeichnis toEntity(WaehlerverzeichnisModel waehlerverzeichnisModel);

    WaehlerverzeichnisModel toModel(Waehlerverzeichnis entity);
}
