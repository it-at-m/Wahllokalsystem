package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.eai.model.WahlvorschlaegeDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WLSWahlvorschlaege;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import org.mapstruct.Mapper;

@Mapper
public interface WahlvorschlaegeClientWLSMapper {

    default WLSWahlvorschlaege fromClientDTOtoWLSEntity(WahlvorschlaegeDTO clientDTO) throws JsonProcessingException {

        WLSWahlvorschlaege entity = new WLSWahlvorschlaege();
        ObjectMapper jacksonMapper = new ObjectMapper();
        entity.setBezirkUndWahlID(new BezirkUndWahlID(clientDTO.getWahlbezirkID(), clientDTO.getWahlID()));
        entity.setWahlvorschlaegeAsJson(jacksonMapper.writeValueAsString(clientDTO));

        return entity;
    }
}
