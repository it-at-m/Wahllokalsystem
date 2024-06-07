package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.unterbrechungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UnterbrechungsUhrzeit;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.common.WahlurneModelMapper;
import org.mapstruct.Mapper;

@Mapper(uses = WahlurneModelMapper.class)
public interface UnterbrechungsUhrzeitModelMapper {

    UnterbrechungsUhrzeitModel toModel(UnterbrechungsUhrzeit entity);

    UnterbrechungsUhrzeit toEntity(UnterbrechungsUhrzeitModel model);
}
