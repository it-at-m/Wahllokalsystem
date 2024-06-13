package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.fortsetzungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.FortsetzungsUhrzeit;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UnterbrechungsUhrzeit;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.unterbrechungsuhrzeit.UnterbrechungsUhrzeitModel;
import org.mapstruct.Mapper;

@Mapper
public interface FortsetzungsUhrzeitModelMapper {

    FortsetzungsUhrzeitModel toModel(FortsetzungsUhrzeit entity);

    FortsetzungsUhrzeit toEntity(FortsetzungsUhrzeitModel model);
}
