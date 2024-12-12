package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.monitoring.model.DruckdatenDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.monitoring.model.SendungsdatenDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface StatusClientMapper {

    @Mapping(target = "druckuhrzeit", source = "dateTimeOfEvent")
    DruckdatenDTO toDruckdatenDTO(BezirkUndWahlID bezirkUndWahlID, LocalDateTime dateTimeOfEvent);

    @Mapping(target = "sendungsuhrzeit", source = "dateTimeOfEvent")
    SendungsdatenDTO toSendungsdatenDTO(BezirkUndWahlID bezirkUndWahlID, LocalDateTime dateTimeOfEvent);
}
