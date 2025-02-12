package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.basisdaten;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.basisdaten.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlartModel;
import org.mapstruct.Mapper;

@Mapper
public interface BasisdatenClientMapper {

    WahlartModel toModel(WahlDTO.WahlartEnum wahlart);
}
