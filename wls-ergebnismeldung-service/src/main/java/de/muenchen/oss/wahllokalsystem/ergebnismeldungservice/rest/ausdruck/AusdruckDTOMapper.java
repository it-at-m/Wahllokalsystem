package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndMeldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckReadModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckWriteModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AusdruckDTOMapper {

    @Mapping(source = "wahlUndBezirkIDUndMeldungsartModel.wahlID", target = "wahlID")
    @Mapping(source = "wahlUndBezirkIDUndMeldungsartModel.wahlbezirkID", target = "wahlbezirkID")
    @Mapping(source = "wahlUndBezirkIDUndMeldungsartModel.meldungsartModel", target = "meldungsart")
    AusdruckReadDTO toDTO(AusdruckReadModel ausdruckModel);

    @Mapping(target = "wahlUndBezirkIDUndMeldungsartModel.wahlID", source = "wahlUndBezirkIDUndMeldungsart.wahlID")
    @Mapping(target = "wahlUndBezirkIDUndMeldungsartModel.wahlbezirkID", source = "wahlUndBezirkIDUndMeldungsart.wahlbezirkID")
    @Mapping(target = "wahlUndBezirkIDUndMeldungsartModel.meldungsartModel", source = "wahlUndBezirkIDUndMeldungsart.meldungsart")
    AusdruckWriteModel toModel(AusdruckWriteDTO ausdruckWriteDTO, WahlUndBezirkIDUndMeldungsart wahlUndBezirkIDUndMeldungsart);
}
