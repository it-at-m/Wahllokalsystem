package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahllokalZustand;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahllokalzustand.Druckzustand;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahllokalzustand.WahllokalZustand;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.dto.DruckzustandDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.dto.WahllokalZustandDTO;
import java.time.LocalDateTime;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WahllokalZustandMapper {

  WahllokalZustandDTO toDTO(WahllokalZustand entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "teamID", ignore = true)
  WahllokalZustand toEntity(WahllokalZustandDTO dto);

  Druckzustand toEntity(DruckzustandDTO druckzustandDTO);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "druckzustaende", ignore = true)
  @Mapping(target = "zuletztGesehen", ignore = true)
  WahllokalZustand toEntityWithLetzteAbmeldung(
      UUID wahlbezirkID, String teamID, LocalDateTime letzteAbmeldung);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "druckzustaende", ignore = true)
  @Mapping(target = "letzteAbmeldung", ignore = true)
  WahllokalZustand toEntityWithLastSeen(
      UUID wahlbezirkID, String teamID, LocalDateTime zuletztGesehen);
}
