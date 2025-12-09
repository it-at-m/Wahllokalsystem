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
  @Mapping(source = "wahlUndBezirkIDUndMeldungsartModel.meldungsart", target = "meldungsart")
  AusdruckReadDTO toDTO(AusdruckReadModel ausdruckModel);

  AusdruckWriteModel toModel(
      AusdruckWriteDTO ausdruckWriteDTO,
      WahlUndBezirkIDUndMeldungsartModel wahlUndBezirkIDUndMeldungsartModel);
}
