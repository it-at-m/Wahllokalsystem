package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.Wahlvorstand;
import org.mapstruct.Mapper;

@Mapper
public interface WahlvorstandModelMapper {

    Wahlvorstand toEntity(final WahlvorstandModel wahlvorstandModel);
}
