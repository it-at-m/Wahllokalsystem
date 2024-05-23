package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.common;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.Wahlurne;
import org.mapstruct.Mapper;

@Mapper
public interface WahlurneModelMapper {

    WahlurneModel toModel(Wahlurne wahlurne);
}
