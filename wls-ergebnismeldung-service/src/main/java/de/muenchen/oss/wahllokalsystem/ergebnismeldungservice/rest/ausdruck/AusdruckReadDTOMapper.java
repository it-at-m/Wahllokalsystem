package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AusdruckReadDTOMapper {

    @Mapping(source = "wahlUndBezirkIDUndMeldungsart.wahlID", target = "wahlID")
    @Mapping(source = "wahlUndBezirkIDUndMeldungsart.wahlbezirkID", target = "wahlbezirkID")
    @Mapping(source = "wahlUndBezirkIDUndMeldungsart.meldungsart", target = "meldungsart")
    AusdruckReadDTO toDTO(AusdruckModel ausdruckModel);
}
