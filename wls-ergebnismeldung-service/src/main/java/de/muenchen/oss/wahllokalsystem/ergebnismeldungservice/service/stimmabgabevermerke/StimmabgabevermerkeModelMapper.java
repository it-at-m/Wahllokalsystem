package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmabgabevermerke;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Vermerk;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Wahldaten;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface StimmabgabevermerkeModelMapper {

    StimmabgabevermerkeModel toModel(Stimmabgabevermerke entity);

    @Mapping(target = "wahlID", source = "bezirkUndWahlIDUndWaehlerverzeichnisnummer.wahlID")
    @Mapping(target = "wahlbezirkID", source = "bezirkUndWahlIDUndWaehlerverzeichnisnummer.wahlbezirkID")
    @Mapping(target = "waehlerverzeichnisNummer", source = "bezirkUndWahlIDUndWaehlerverzeichnisnummer.waehlerverzeichnisNummer")
    WahldatenModel toModel(Wahldaten entity);

    Stimmabgabevermerke toEntity(StimmabgabevermerkeModel model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bezirkUndWahlIDUndWaehlerverzeichnisnummer.wahlID", source = "wahlID")
    @Mapping(target = "bezirkUndWahlIDUndWaehlerverzeichnisnummer.wahlbezirkID", source = "wahlbezirkID")
    @Mapping(target = "bezirkUndWahlIDUndWaehlerverzeichnisnummer.waehlerverzeichnisNummer", source = "waehlerverzeichnisNummer")
    Wahldaten toEntity(WahldatenModel wahldatenModel);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "wahldaten", ignore = true)
    Vermerk toEntity(VermerkModel vermerkModel);
}
