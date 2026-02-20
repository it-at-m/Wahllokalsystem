package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahllokalZustand;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahllokalzustand.Druckzustand;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahllokalzustand.WahllokalZustand;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.dto.DruckzustandDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.dto.WahllokalZustandDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WahllokalZustandMapper {

  @Mapping(target = "id", ignore = true)
  WahllokalZustand toEntity(WahllokalZustandDTO wahllokalZustandDTO);

  Druckzustand toEntity(DruckzustandDTO druckzustandDTO);
}
