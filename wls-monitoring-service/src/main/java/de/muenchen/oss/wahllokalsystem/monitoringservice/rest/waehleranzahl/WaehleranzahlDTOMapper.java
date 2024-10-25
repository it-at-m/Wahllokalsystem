package de.muenchen.oss.wahllokalsystem.monitoringservice.rest.waehleranzahl;

import de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl.WaehleranzahlModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import org.mapstruct.Mapper;

@Mapper
public interface WaehleranzahlDTOMapper {

    WaehleranzahlDTO toDTO(WaehleranzahlModel waehleranzahlModel);

    WaehleranzahlModel toSetModel(BezirkUndWahlID bezirkUndWahlID, WaehleranzahlDTO waehleranzahlDTO);
}
