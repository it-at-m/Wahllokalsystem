package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlschliessungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UrnenwahlSchliessungsUhrzeit;
import org.mapstruct.Mapper;

@Mapper
public interface UrnenwahlSchliessungsUhrzeitModelMapper {

    UrnenwahlSchliessungsUhrzeitModel toModel(UrnenwahlSchliessungsUhrzeit entity);

    UrnenwahlSchliessungsUhrzeit toEntity(UrnenwahlSchliessungsUhrzeitModel model);
}
