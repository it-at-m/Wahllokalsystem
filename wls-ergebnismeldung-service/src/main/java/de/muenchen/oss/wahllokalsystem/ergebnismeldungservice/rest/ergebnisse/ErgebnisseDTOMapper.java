package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnisse;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseReference;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ErgebnisseDTOMapper {

    @Mapping(target = "wahlbezirkID", source = "bezirkUndWahlIDStapelart.wahlbezirkID")
    @Mapping(target = "wahlID", source = "bezirkUndWahlIDStapelart.wahlID")
    @Mapping(target = "stapelart", source = "bezirkUndWahlIDStapelart.stapelart")
    ErgebnisseModel toModel(ErgebnisseDTO dto);

    @Mapping(target = "bezirkUndWahlIDStapelart.wahlbezirkID", source = "wahlbezirkID")
    @Mapping(target = "bezirkUndWahlIDStapelart.wahlID", source = "wahlID")
    @Mapping(target = "bezirkUndWahlIDStapelart.stapelart", source = "stapelart")
    ErgebnisseDTO toDTO(ErgebnisseModel entity);

    ErgebnisseReference toReferenceModel(String wahlbezirkID, String wahlID, Stapelart stapelart);
}
