package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke.dto;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.StimmabgabevermerkeModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.WahldatenModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface StimmabgabevermerkeDTOMapper {

    @Mapping(source = "bezirkIDUndWaehlerverzeichnisNummer.waehlerverzeichnisNummer", target = "waehlerverzeichnisNummer")
    @Mapping(source = "bezirkIDUndWaehlerverzeichnisNummer.wahlbezirkID", target = "wahlbezirkID")
    StimmabgabevermerkeDTO toStimmabgabevermerkeDTO(StimmabgabevermerkeModel stimmabgabevermerkeModel);

    @Mapping(source = "bezirkUndWahlIDUndWaehlerverzeichnisnummer.wahlbezirkID", target = "wahlbezirkID")
    @Mapping(source = "bezirkUndWahlIDUndWaehlerverzeichnisnummer.wahlID", target = "wahlID")
    @Mapping(source = "bezirkUndWahlIDUndWaehlerverzeichnisnummer.waehlerverzeichnisNummer", target = "waehlerverzeichnisNummer")
    WahldatenDTO toWahldatenDto(WahldatenModel wahldatenModel);

    @Mapping(source = "wahlbezirkID", target = "bezirkUndWahlIDUndWaehlerverzeichnisnummer.wahlbezirkID")
    @Mapping(source = "wahlID", target = "bezirkUndWahlIDUndWaehlerverzeichnisnummer.wahlID")
    @Mapping(source = "waehlerverzeichnisNummer", target = "bezirkUndWahlIDUndWaehlerverzeichnisnummer.waehlerverzeichnisNummer")
    WahldatenModel toWahldatenModel(WahldatenDTO wahldatenDTO);

    @Mapping(source = "waehlerverzeichnisNummer", target = "bezirkIDUndWaehlerverzeichnisNummer.waehlerverzeichnisNummer")
    @Mapping(source = "wahlbezirkID", target = "bezirkIDUndWaehlerverzeichnisNummer.wahlbezirkID")
    StimmabgabevermerkeModel toStimmabgabevermerkeModel(StimmabgabevermerkeDTO stimmabgabevermerkeDTO);

}
