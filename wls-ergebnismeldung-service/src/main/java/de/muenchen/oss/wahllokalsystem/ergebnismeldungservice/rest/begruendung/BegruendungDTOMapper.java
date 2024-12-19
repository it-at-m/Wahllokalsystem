package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.begruendung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.begruendung.BegruendungModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.begruendung.BegruendungReference;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BegruendungDTOMapper {

    @Mapping(target = "bezirkUndWahlIDStapelart.wahlbezirkID", source = "wahlbezirkID")
    @Mapping(target = "bezirkUndWahlIDStapelart.wahlID", source = "wahlID")
    @Mapping(target = "bezirkUndWahlIDStapelart.stapelart", source = "stapelart")
    BegruendungDTO toDTO(BegruendungModel model);

    BegruendungReference toReferenceModel(String wahlbezirkID, String wahlID, Stapelart stapelart);

    BegruendungModel toModel(BegruendungDTO dto, String wahlbezirkID, String wahlID, Stapelart stapelart);
}
