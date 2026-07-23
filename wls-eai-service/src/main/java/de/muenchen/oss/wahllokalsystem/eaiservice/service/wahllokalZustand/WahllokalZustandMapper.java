package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahllokalZustand;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahllokalzustand.Druckzustand;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahllokalzustand.WahllokalZustand;
import de.muenchen.oss.wahllokalsystem.eaiservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.dto.DruckzustandDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.dto.WahllokalZustandDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDateTime;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class WahllokalZustandMapper {

  @Autowired protected ExceptionFactory exceptionFactory;

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "teamID", ignore = true)
  public abstract WahllokalZustand toEntity(WahllokalZustandDTO wahllokalZustandDTO);

  public abstract Druckzustand toEntity(DruckzustandDTO druckzustandDTO);

  public WahllokalZustand toEntityWithLastSeen(
      final String wahlbezirkID, final String teamID, final LocalDateTime timestamp) {
    final UUID parsedWahlbezirkID;
    try {
      parsedWahlbezirkID = UUID.fromString(wahlbezirkID);
    } catch (IllegalArgumentException e) {
      throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.ID_NICHT_KONVERTIERBAR);
    }

    WahllokalZustand w = new WahllokalZustand();
    w.setWahlbezirkID(parsedWahlbezirkID);
    w.setTeamID(teamID);
    w.setZuletztGesehen(timestamp);
    return w;
  }

  public WahllokalZustand toEntityWithLetzteAbmeldung(
      final String wahlbezirkID, final String teamID, final LocalDateTime timestamp) {
    final UUID parsedWahlbezirkID;
    try {
      parsedWahlbezirkID = UUID.fromString(wahlbezirkID);
    } catch (IllegalArgumentException e) {
      throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.ID_NICHT_KONVERTIERBAR);
    }

    WahllokalZustand w = new WahllokalZustand();
    w.setWahlbezirkID(parsedWahlbezirkID);
    w.setTeamID(teamID);
    w.setLetzteAbmeldung(timestamp);
    return w;
  }
}
