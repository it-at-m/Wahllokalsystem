package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.begruendung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung.Begruendung;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung.BezirkUndWahlIDStapelart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BegruendungModelMapper {

    BezirkUndWahlIDStapelart toEmbeddedId(BegruendungReference reference);

    @Mapping(target = "wahlbezirkID", source = "bezirkUndWahlIDStapelart.wahlbezirkID")
    @Mapping(target = "wahlID", source = "bezirkUndWahlIDStapelart.wahlID")
    @Mapping(target = "stapelart", source = "bezirkUndWahlIDStapelart.stapelart")
    BegruendungModel toModel(Begruendung entity);

    @Mapping(target = "bezirkUndWahlIDStapelart.wahlbezirkID", source = "wahlbezirkID")
    @Mapping(target = "bezirkUndWahlIDStapelart.wahlID", source = "wahlID")
    @Mapping(target = "bezirkUndWahlIDStapelart.stapelart", source = "stapelart")
    Begruendung toEntity(BegruendungModel model);
}
