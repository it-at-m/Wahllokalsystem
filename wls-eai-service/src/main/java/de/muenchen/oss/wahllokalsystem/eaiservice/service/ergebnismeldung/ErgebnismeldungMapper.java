package de.muenchen.oss.wahllokalsystem.eaiservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung.Ergebnis;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung.Ergebnismeldung;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.dto.ErgebnisDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.dto.ErgebnismeldungDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface ErgebnismeldungMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "AWerte", source = "aWerte")
    @Mapping(target = "BWerte", source = "bWerte")
    @Mapping(target = "ergebnisse", qualifiedByName = "mapWithoutErstellungszeit")
    Ergebnismeldung toEntity(ErgebnismeldungDTO dto);

    @Named("mapWithoutErstellungszeit")
    @Mapping(target = "erstellungszeit", ignore = true)
    Ergebnis toEntity(ErgebnisDTO dto);
}
