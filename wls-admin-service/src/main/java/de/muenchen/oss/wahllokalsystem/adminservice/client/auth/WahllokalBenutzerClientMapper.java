package de.muenchen.oss.wahllokalsystem.adminservice.client.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.auth.model.WahllokalUserInfoDTO;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahllokalbenutzer.TripleOfWahlbezirkIDWahlnummerWahlIDModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahllokalbenutzer.WahllokalBenutzerModel;
import java.util.List;
import lombok.val;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WahllokalBenutzerClientMapper {
  ObjectMapper objectMapper = new ObjectMapper();

  @Mapping(target = "wbidWahlnummer", source = "wbid_wahlnummer")
  WahllokalUserInfoDTO toDTO(WahllokalBenutzerModel wahllokalBenutzerModel);

  default String mapTripleToJsonAsString(
      final List<TripleOfWahlbezirkIDWahlnummerWahlIDModel> wbid_wahlnummer)
      throws JsonProcessingException {
    val root = objectMapper.createObjectNode();
    root.set("wbid_wahlnummer", objectMapper.valueToTree(wbid_wahlnummer));
    return objectMapper.writeValueAsString(root);
  }

  List<WahllokalUserInfoDTO> toDTO(List<WahllokalBenutzerModel> wahllokalBenutzerModelList);
}
