package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndMeldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.AusdruckModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AusdruckWriteDTOMapper {

    @Mapping(target = "erstelltAm", ignore = true)
    AusdruckModel toModel(AusdruckWriteDTO ausdruckWriteDTO, WahlUndBezirkIDUndMeldungsart wahlUndBezirkIDUndMeldungsart);

}
