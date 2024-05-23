package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.waehlerverzeichnis;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.waehlerverzeichnis.WaehlerverzeichnisModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WaehlerverzeichnisDTOMapper {

    @Mapping(target = "bezirkIDUndWaehlerverzeichnisNummer", source = "waehlerverzeichnisReference")
    WaehlerverzeichnisModel toModel(BezirkIDUndWaehlerverzeichnisNummer waehlerverzeichnisReference, WaehlerverzeichnisWriteDTO waehlerverzeichnisWriteDTO);

    WaehlerverzeichnisDTO toDto(WaehlerverzeichnisModel waehlerverzeichnisModel);
}
