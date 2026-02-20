package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Ausdruck;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndMeldungsart;
import java.time.Instant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AusdruckModelMapper {

  @Mapping(target = "wahlUndBezirkIDUndMeldungsartModel", source = "wahlUndBezirkIDUndMeldungsart")
  AusdruckReadModel toModel(Ausdruck entity);

  @Mapping(
      target = "wahlUndBezirkIDUndMeldungsart",
      source = "model.wahlUndBezirkIDUndMeldungsartModel")
  Ausdruck toEntity(AusdruckWriteModel model, Instant erstelltAm);

  WahlUndBezirkIDUndMeldungsartModel toModel(WahlUndBezirkIDUndMeldungsart entity);

  WahlUndBezirkIDUndMeldungsart toEntity(WahlUndBezirkIDUndMeldungsartModel model);
}
