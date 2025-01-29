package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.begruendung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common.StapelartDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.begruendung.BegruendungModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.begruendung.BegruendungReference;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BegruendungDTOMapper {

    @Mapping(target = "bezirkUndWahlIDStapelart.wahlbezirkID", source = "wahlbezirkID")
    @Mapping(target = "bezirkUndWahlIDStapelart.wahlID", source = "wahlID")
    @Mapping(target = "bezirkUndWahlIDStapelart.stapelartDTO", source = "stapelart")
    @Mapping(target = "grund", source = "grund1")
    BegruendungDTO toDTO(BegruendungModel model);

    BegruendungReference toReferenceModel(String wahlbezirkID, String wahlID, StapelartDTO stapelart);

    @Mapping(target = "wahlbezirkID", source = "bezirkUndWahlIDStapelart.wahlbezirkID")
    @Mapping(target = "wahlID", source = "bezirkUndWahlIDStapelart.wahlID")
    @Mapping(target = "stapelart", source = "bezirkUndWahlIDStapelart.stapelartDTO")
    @Mapping(target = "grund1", source = "grund")
    BegruendungModel toModel(BegruendungDTO dto);

    Stapelart toStapelart(StapelartDTO stapelart);
}
