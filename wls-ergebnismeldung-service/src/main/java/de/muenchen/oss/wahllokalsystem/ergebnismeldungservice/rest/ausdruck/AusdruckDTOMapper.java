package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndMeldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckReadModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckWriteModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AusdruckDTOMapper {

    @Mapping(source = "wahlUndBezirkIDUndMeldungsart.wahlID", target = "wahlID")
    @Mapping(source = "wahlUndBezirkIDUndMeldungsart.wahlbezirkID", target = "wahlbezirkID")
    @Mapping(source = "wahlUndBezirkIDUndMeldungsart.meldungsart", target = "meldungsart")
    AusdruckReadDTO toDTO(AusdruckReadModel ausdruckModel);

    AusdruckWriteModel toModel(AusdruckWriteDTO ausdruckWriteDTO, WahlUndBezirkIDUndMeldungsart wahlUndBezirkIDUndMeldungsart);
}
