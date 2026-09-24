package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Ausdruck;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndDokumentart;
import java.time.Instant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AusdruckModelMapper {

  @Mapping(target = "wahlUndBezirkIDUndDokumentartModel", source = "wahlUndBezirkIDUndDokumentart")
  AusdruckReadModel toModel(Ausdruck entity);

  @Mapping(
      target = "wahlUndBezirkIDUndDokumentart",
      source = "model.wahlUndBezirkIDUndDokumentartModel")
  Ausdruck toEntity(AusdruckWriteModel model, Instant erstelltAm);

  WahlUndBezirkIDUndDokumentartModel toModel(WahlUndBezirkIDUndDokumentart entity);

  WahlUndBezirkIDUndDokumentart toEntity(WahlUndBezirkIDUndDokumentartModel model);
}
