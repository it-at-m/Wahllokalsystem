package de.muenchen.oss.wahllokalsystem.monitoringservice.rest.waehleranzahl;

import de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl.WaehleranzahlModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WaehleranzahlDTOMapper {

    @Mapping(source = "bezirkUndWahlID.wahlID", target = "wahlID")
    @Mapping(source = "bezirkUndWahlID.wahlbezirkID", target = "wahlbezirkID")
    WaehleranzahlDTO toDTO(WaehleranzahlModel waehleranzahlModel);

    WaehleranzahlModel toSetModel(BezirkUndWahlID bezirkUndWahlID, WaehleranzahlDTO waehleranzahlDTO);
}
