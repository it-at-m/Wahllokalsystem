package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UrnenwahlVorbereitung;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.common.WahlurneModelMapper;
import org.mapstruct.Mapper;

@Mapper(uses = WahlurneModelMapper.class)
public interface UrnenwahlvorbereitungModelMapper {

    UrnenwahlvorbereitungModel toModel(UrnenwahlVorbereitung entity);

    UrnenwahlVorbereitung toEntity(UrnenwahlvorbereitungModel model);
}
