package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import org.mapstruct.Mapper;

@Mapper
public interface StapelartModelMapper {
  StapelartModel toModel(Stapelart stapelart);

  Stapelart toEntity(StapelartModel stapelartModel);
}
