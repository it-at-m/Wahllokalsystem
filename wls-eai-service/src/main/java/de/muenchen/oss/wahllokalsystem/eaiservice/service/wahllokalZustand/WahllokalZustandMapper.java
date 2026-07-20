package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahllokalZustand;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahllokalzustand.Druckzustand;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahllokalzustand.WahllokalZustand;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.dto.DruckzustandDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.dto.WahllokalZustandDTO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(builder = @Builder(disableBuilder = true))
public interface WahllokalZustandMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "teamID", ignore = true)
  WahllokalZustand toEntity(WahllokalZustandDTO wahllokalZustandDTO);

  Druckzustand toEntity(DruckzustandDTO druckzustandDTO);
}
