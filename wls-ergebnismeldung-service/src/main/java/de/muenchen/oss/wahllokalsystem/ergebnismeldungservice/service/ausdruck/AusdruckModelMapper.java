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

    @Mapping(target = "wahlUndBezirkIDUndMeldungsart.meldungsart", source = "model.wahlUndBezirkIDUndMeldungsartModel.meldungsartModel")
    Ausdruck toEntity(AusdruckWriteModel model, Instant erstelltAm);

    @Mapping(target = "meldungsartModel", source = "meldungsart")
    WahlUndBezirkIDUndMeldungsartModel toModel(WahlUndBezirkIDUndMeldungsart entity);

    @Mapping(target = "meldungsart", source = "meldungsartModel")
    WahlUndBezirkIDUndMeldungsart toEntity(WahlUndBezirkIDUndMeldungsartModel model);
}
