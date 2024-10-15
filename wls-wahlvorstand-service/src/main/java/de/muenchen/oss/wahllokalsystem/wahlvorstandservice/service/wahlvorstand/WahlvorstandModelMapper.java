package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.wahlvorstand.Wahlvorstand;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.wahlvorstand.Wahlvorstandsmitglied;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.aoueaiClient.WahlvorstandModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WahlvorstandModelMapper {

    WahlvorstandModel toModel(final Wahlvorstand entity);

    @Mapping(target = "wahlvorstand", ignore = true)
    Wahlvorstandsmitglied toEntity(final WahlvorstandsmitgliedModel model);

    Wahlvorstand toEntity(final WahlvorstandModel model);
}
