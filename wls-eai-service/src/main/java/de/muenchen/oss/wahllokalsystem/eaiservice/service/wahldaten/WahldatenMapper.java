package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahldaten;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Stimmzettelgebiet;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Stimmzettelgebietsart;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahl;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahlart;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahlbezirk;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.WahlbezirkArt;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahltag;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.dto.WahlartDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.BasisstrukturdatenDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.StimmzettelgebietDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.StimmzettelgebietsartDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlberechtigteDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlbezirkArtDTO;
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
    @Mapping(target = "nummer", source = "wahltag.nummer")
    WahlDTO toDTO(Wahl wahl);

    Wahlart toModel(WahlartDTO wahlart);

    @Mapping(target = "identifikator", source = "id")
    @Mapping(target = "wahltag", source = "stimmzettelgebiet.wahl.wahltag.tag")
    @Mapping(target = "wahlID", source = "stimmzettelgebiet.wahl.id")
    @Mapping(target = "wahlnummer", source = "stimmzettelgebiet.wahl.wahltag.nummer")
    WahlbezirkDTO toDTO(Wahlbezirk wahlbezirk);

    WahlbezirkArt toEntity(WahlbezirkArtDTO dto);

    @Mapping(target = "identifikator", source = "id")
    @Mapping(target = "wahltag", source = "wahl.wahltag.tag")
    StimmzettelgebietDTO toDTO(Stimmzettelgebiet stimmzettelgebiet);

    Stimmzettelgebietsart toEntity(StimmzettelgebietsartDTO dto);

    @Mapping(target = "wahlID", source = "stimmzettelgebiet.wahl.id")
    @Mapping(target = "wahlbezirkID", source = "id")
    WahlberechtigteDTO toWahlberechtigteDTO(Wahlbezirk wahlbezirk);

    @Mapping(target = "wahlID", source = "stimmzettelgebiet.wahl.id")
    @Mapping(target = "wahltag", source = "stimmzettelgebiet.wahl.wahltag.tag")
    @Mapping(target = "wahlbezirkID", source = "id")
    @Mapping(target = "stimmzettelgebietID", source = "stimmzettelgebiet.id")
    BasisstrukturdatenDTO toBasisstrukturdatenDTO(Wahlbezirk wahlbezirk);
}
