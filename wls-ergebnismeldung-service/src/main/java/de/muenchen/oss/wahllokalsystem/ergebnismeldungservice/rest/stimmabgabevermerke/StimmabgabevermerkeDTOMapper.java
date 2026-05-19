package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.StimmabgabevermerkeModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.WahldatenModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface StimmabgabevermerkeDTOMapper {

  @Mapping(
      source = "bezirkIDUndWaehlerverzeichnisNummer.waehlerverzeichnisNummer",
      target = "waehlerverzeichnisNummer")
  @Mapping(source = "bezirkIDUndWaehlerverzeichnisNummer.wahlbezirkID", target = "wahlbezirkID")
  StimmabgabevermerkeDTO toStimmabgabevermerkeDTO(
      StimmabgabevermerkeModel stimmabgabevermerkeModel);

  @Mapping(
      source = "waehlerverzeichnisNummer",
      target = "bezirkIDUndWaehlerverzeichnisNummer.waehlerverzeichnisNummer")
  @Mapping(source = "wahlbezirkID", target = "bezirkIDUndWaehlerverzeichnisNummer.wahlbezirkID")
  StimmabgabevermerkeModel toStimmabgabevermerkeModel(
      StimmabgabevermerkeDTO stimmabgabevermerkeDTO);

  WahldatenDTO toWahldatenDTO(WahldatenModel wahldatenModel);

  @Mapping(source = "wahlID", target = "wahlID")
  @Mapping(source = "wahlbezirkID", target = "wahlbezirkID")
  @Mapping(source = "waehlerverzeichnisNummer", target = "waehlerverzeichnisNummer")
  WahldatenModel toWahldatenModel(
      String wahlID, String wahlbezirkID, long waehlerverzeichnisNummer, WahldatenDTO dto);
}
