package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnisse;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseReference;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ErgebnisseDTOMapper {

    @Mapping(target = "wahlbezirkID", source = "bezirkUndWahlIDStapelartDTO.wahlbezirkID")
    @Mapping(target = "wahlID", source = "bezirkUndWahlIDStapelartDTO.wahlID")
    @Mapping(target = "stapelart", source = "bezirkUndWahlIDStapelartDTO.stapelartDTO")
    ErgebnisseModel toModel(ErgebnisseDTO dto);

    @Mapping(target = "bezirkUndWahlIDStapelartDTO.wahlbezirkID", source = "wahlbezirkID")
    @Mapping(target = "bezirkUndWahlIDStapelartDTO.wahlID", source = "wahlID")
    @Mapping(target = "bezirkUndWahlIDStapelartDTO.stapelartDTO", source = "stapelart")
    ErgebnisseDTO toDTO(ErgebnisseModel entity);

    ErgebnisseReference toReferenceModel(String wahlbezirkID, String wahlID, StapelartDTO stapelart);

    Stapelart toSpapelart(StapelartDTO stapelart);
}
