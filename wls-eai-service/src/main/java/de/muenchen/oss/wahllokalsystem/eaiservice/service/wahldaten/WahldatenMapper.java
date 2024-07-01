package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahldaten;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Stimmzettelgebiet;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahl;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahlbezirk;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahltag;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.BasisstrukturdatenDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.StimmzettelgebietDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlberechtigteDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlbezirkDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahltagDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WahldatenMapper {

    @Mapping(target = "identifikator", source = "id")
    WahltagDTO toDTO(Wahltag wahltag);

    @Mapping(target = "identifikator", source = "id")
    @Mapping(target = "wahltag", source = "wahltag.tag")
    WahlDTO toDTO(Wahl wahl);

    @Mapping(target = "identifikator", source = "id")
    @Mapping(target = "wahltag", source = "stimmzettelgebiet.wahl.wahltag.tag")
    @Mapping(target = "wahlID", source = "stimmzettelgebiet.wahl.id")
    @Mapping(target = "wahlnummer", source = "stimmzettelgebiet.wahl.nummer")
    WahlbezirkDTO toDTO(Wahlbezirk wahlbezirk);

    @Mapping(target = "identifikator", source = "id")
    @Mapping(target = "wahltag", source = "wahl.wahltag.tag")
    StimmzettelgebietDTO toDTO(Stimmzettelgebiet stimmzettelgebiet);

    @Mapping(target = "wahlID", source = "stimmzettelgebiet.wahl.id")
    @Mapping(target = "wahlbezirkID", source = "id")
    WahlberechtigteDTO toWahlberechtigteDTO(Wahlbezirk wahlbezirk);

    @Mapping(target = "wahlID", source = "stimmzettelgebiet.wahl.id")
    @Mapping(target = "wahltag", source = "stimmzettelgebiet.wahl.wahltag.tag")
    @Mapping(target = "wahlbezirkID", source = "id")
    @Mapping(target = "stimmzettelgebietID", source = "stimmzettelgebiet.id")
    BasisstrukturdatenDTO toBasisstrukturdatenDTO(Wahlbezirk wahlbezirk);
}
