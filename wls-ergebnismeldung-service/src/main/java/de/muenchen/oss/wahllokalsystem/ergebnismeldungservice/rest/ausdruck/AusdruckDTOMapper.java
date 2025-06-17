package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckReadModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckWriteModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.WahlUndBezirkIDUndMeldungsartModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AusdruckDTOMapper {

    @Mapping(source = "wahlUndBezirkIDUndMeldungsartModel.wahlID", target = "wahlID")
    @Mapping(source = "wahlUndBezirkIDUndMeldungsartModel.wahlbezirkID", target = "wahlbezirkID")
    @Mapping(source = "wahlUndBezirkIDUndMeldungsartModel.meldungsartModel", target = "meldungsart")
    AusdruckReadDTO toDTO(AusdruckReadModel ausdruckModel);

    @Mapping(target = "wahlUndBezirkIDUndMeldungsartModel.wahlID", source = "wahlUndBezirkIDUndMeldungsartDto.wahlID")
    @Mapping(target = "wahlUndBezirkIDUndMeldungsartModel.wahlbezirkID", source = "wahlUndBezirkIDUndMeldungsartDto.wahlbezirkID")
    @Mapping(target = "wahlUndBezirkIDUndMeldungsartModel.meldungsartModel", source = "wahlUndBezirkIDUndMeldungsartDto.meldungsartDto")
    AusdruckWriteModel toModel(AusdruckWriteDTO ausdruckWriteDTO, WahlUndBezirkIDUndMeldungsartDTO wahlUndBezirkIDUndMeldungsartDto);

    @Mapping(target = "meldungsartDto", source = "meldungsartModel")
    WahlUndBezirkIDUndMeldungsartDTO toDTO(WahlUndBezirkIDUndMeldungsartModel model);

    @Mapping(target = "meldungsartModel", source = "meldungsartDto")
    WahlUndBezirkIDUndMeldungsartModel toModel(WahlUndBezirkIDUndMeldungsartDTO dto);
}
