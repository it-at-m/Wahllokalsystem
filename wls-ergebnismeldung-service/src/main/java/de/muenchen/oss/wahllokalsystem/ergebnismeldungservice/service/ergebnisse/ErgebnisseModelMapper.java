package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung.BezirkUndWahlIDStapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.Ergebnisse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ErgebnisseModelMapper {

    BezirkUndWahlIDStapelart toEmbeddedId(ErgebnisseReference reference);

    @Mapping(target = "wahlbezirkID", source = "bezirkUndWahlIDStapelart.wahlbezirkID")
    @Mapping(target = "wahlID", source = "bezirkUndWahlIDStapelart.wahlID")
    @Mapping(target = "stapelart", source = "bezirkUndWahlIDStapelart.stapelart")
    ErgebnisseModel toModel(Ergebnisse entity);

    @Mapping(target = "bezirkUndWahlIDStapelart.wahlbezirkID", source = "wahlbezirkID")
    @Mapping(target = "bezirkUndWahlIDStapelart.wahlID", source = "wahlID")
    @Mapping(target = "bezirkUndWahlIDStapelart.stapelart", source = "stapelart")
    Ergebnisse toEntity(ErgebnisseModel model);
}
