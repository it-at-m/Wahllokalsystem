package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.waehlerverzeichnis;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.waehlerverzeichnis.WaehlerverzeichnisModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WaehlerverzeichnisDTOMapper {

    @Mapping(target = "bezirkIDUndWaehlerverzeichnisNummer.wahlbezirkID", source = "wahlbezirkID")
    @Mapping(target = "bezirkIDUndWaehlerverzeichnisNummer.waehlerverzeichnisNummer", source = "waehlerverzeichnisNummer")
    WaehlerverzeichnisModel toModel(String wahlbezirkID, Long waehlerverzeichnisNummer, WaehlerverzeichnisWriteDTO waehlerverzeichnisWriteDTO);

    @Mapping(target = "wahlbezirkID", source = "wahlbezirkID")
    @Mapping(target = "waehlerverzeichnisNummer", source = "waehlerverzeichnisNummer")
    BezirkIDUndWaehlerverzeichnisNummer toReference(String wahlbezirkID, Long waehlerverzeichnisNummer);

    WaehlerverzeichnisDTO toDto(WaehlerverzeichnisModel waehlerverzeichnisModel);
}
