package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndMeldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckModel;
import java.time.Instant;
import org.mapstruct.Mapper;

@Mapper
public interface AusdruckWriteDTOMapper {

    AusdruckModel toModel(AusdruckWriteDTO ausdruckWriteDTO, WahlUndBezirkIDUndMeldungsart wahlUndBezirkIDUndMeldungsart, Instant erstelltAm);

}
