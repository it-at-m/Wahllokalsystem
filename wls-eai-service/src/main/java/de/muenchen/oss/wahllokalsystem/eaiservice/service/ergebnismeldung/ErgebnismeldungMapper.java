package de.muenchen.oss.wahllokalsystem.eaiservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung.Ergebnismeldung;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung.UngueltigeStimmzettel;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.dto.ErgebnismeldungDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface ErgebnismeldungMapper {

    @Mapping(target = "ungueltigeStimmzettels", qualifiedByName = "formatUngueltigeStimmzettel")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "AWerte", source = "aWerte")
    @Mapping(target = "BWerte", source = "bWerte")
    Ergebnismeldung toEntity(ErgebnismeldungDTO dto);

    @Named("formatUngueltigeStimmzettel")
    public static UngueltigeStimmzettel formatUngueltigeStimmzettel(UngueltigeStimmzettel ungueltigeStimmzettel){
        return ungueltigeStimmzettel;
    }

}
