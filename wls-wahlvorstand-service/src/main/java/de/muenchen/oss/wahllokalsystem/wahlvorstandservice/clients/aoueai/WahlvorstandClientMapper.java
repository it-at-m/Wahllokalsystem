package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.clients.aoueai;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.eai.aou.model.WahlvorstandDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.WahlvorstandModel;
import org.mapstruct.Mapper;

@Mapper
public interface WahlvorstandClientMapper {

    WahlvorstandModel toModel(WahlvorstandDTO wahlvorstandDTO);
}
