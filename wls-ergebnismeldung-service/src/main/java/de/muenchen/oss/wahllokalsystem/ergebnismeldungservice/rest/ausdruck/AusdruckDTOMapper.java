package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckReadModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckWriteModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.WahlUndBezirkIDUndDokumentartModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AusdruckDTOMapper {

  @Mapping(source = "wahlUndBezirkIDUndDokumentartModel.wahlID", target = "wahlID")
  @Mapping(source = "wahlUndBezirkIDUndDokumentartModel.wahlbezirkID", target = "wahlbezirkID")
  @Mapping(source = "wahlUndBezirkIDUndDokumentartModel.dokumentart", target = "dokumentart")
  AusdruckReadDTO toDTO(AusdruckReadModel ausdruckModel);

  AusdruckWriteModel toModel(
      AusdruckWriteDTO ausdruckWriteDTO,
      WahlUndBezirkIDUndDokumentartModel wahlUndBezirkIDUndDokumentartModel);
}
