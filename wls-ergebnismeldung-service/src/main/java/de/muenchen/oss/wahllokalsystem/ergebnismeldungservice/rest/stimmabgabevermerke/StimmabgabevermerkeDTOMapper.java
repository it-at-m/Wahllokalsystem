package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.StimmabgabevermerkeModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface StimmabgabevermerkeDTOMapper {

  StimmabgabevermerkeDTO toWahldatenDTO(StimmabgabevermerkeModel stimmabgabevermerkeModel);

  @Mapping(source = "wahlID", target = "wahlID")
  @Mapping(source = "wahlbezirkID", target = "wahlbezirkID")
  @Mapping(source = "waehlerverzeichnisNummer", target = "waehlerverzeichnisNummer")
  StimmabgabevermerkeModel toWahldatenModel(
      String wahlID, String wahlbezirkID, long waehlerverzeichnisNummer, StimmabgabevermerkeDTO dto);
}
