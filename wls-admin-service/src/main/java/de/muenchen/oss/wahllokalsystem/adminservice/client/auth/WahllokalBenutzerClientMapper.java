package de.muenchen.oss.wahllokalsystem.adminservice.client.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.auth.model.WahllokalUserInfoDTO;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahllokalbenutzer.TripleOfWahlbezirkIDWahlNummerWahlIDModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahllokalbenutzer.WahllokalBenutzerModel;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WahllokalBenutzerClientMapper {

    ObjectMapper objectMapper = new ObjectMapper();

    @Mapping(target = "wbidWahlnummer", source = "wbid_wahlnummer")
    WahllokalUserInfoDTO toWahllokalUserInfoDTO(WahllokalBenutzerModel wahllokalBenutzerModel);

    default String map(final ArrayList<TripleOfWahlbezirkIDWahlNummerWahlIDModel> wbid_wahlnummer) throws JsonProcessingException {
        return objectMapper.writeValueAsString(wbid_wahlnummer);
    }

    List<WahllokalUserInfoDTO> toListOfWahllokalUserInfoDTO(List<WahllokalBenutzerModel> wahllokalBenutzerModelList);
}
