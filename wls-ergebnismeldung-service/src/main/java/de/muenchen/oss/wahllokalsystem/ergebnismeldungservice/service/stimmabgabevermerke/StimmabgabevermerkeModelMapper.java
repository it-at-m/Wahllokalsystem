package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmabgabevermerke;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Vermerk;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface StimmabgabevermerkeModelMapper {

  @Mapping(target = "wahlID", source = "bezirkUndWahlIDUndWaehlerverzeichnisnummer.wahlID")
  @Mapping(
      target = "wahlbezirkID",
      source = "bezirkUndWahlIDUndWaehlerverzeichnisnummer.wahlbezirkID")
  @Mapping(
      target = "waehlerverzeichnisNummer",
      source = "bezirkUndWahlIDUndWaehlerverzeichnisnummer.waehlerverzeichnisNummer")
  StimmabgabevermerkeModel toModel(Stimmabgabevermerke entity);

  @Mapping(target = "bezirkUndWahlIDUndWaehlerverzeichnisnummer.wahlID", source = "wahlID")
  @Mapping(
      target = "bezirkUndWahlIDUndWaehlerverzeichnisnummer.wahlbezirkID",
      source = "wahlbezirkID")
  @Mapping(
      target = "bezirkUndWahlIDUndWaehlerverzeichnisnummer.waehlerverzeichnisNummer",
      source = "waehlerverzeichnisNummer")
  Stimmabgabevermerke toEntity(StimmabgabevermerkeModel stimmabgabevermerkeModel);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "stimmabgabevermerke", ignore = true)
  Vermerk toEntity(VermerkModel vermerkModel);
}
