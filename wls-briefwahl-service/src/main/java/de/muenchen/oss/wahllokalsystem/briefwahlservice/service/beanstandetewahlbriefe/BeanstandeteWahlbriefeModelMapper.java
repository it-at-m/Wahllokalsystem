package de.muenchen.oss.wahllokalsystem.briefwahlservice.service.beanstandetewahlbriefe;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.domain.BeanstandeteWahlbriefe;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BeanstandeteWahlbriefeModelMapper {

    BezirkIDUndWaehlerverzeichnisNummer toEmbeddedId(BeanstandeteWahlbriefeReference reference);

    @Mapping(target = "wahlbezirkID", source = "bezirkIDUndWaehlerverzeichnisNummer.wahlbezirkID")
    @Mapping(target = "waehlerverzeichnisNummer", source = "bezirkIDUndWaehlerverzeichnisNummer.waehlerverzeichnisNummer")
    BeanstandeteWahlbriefeModel toModel(BeanstandeteWahlbriefe entity);

    @Mapping(target = "bezirkIDUndWaehlerverzeichnisNummer.waehlerverzeichnisNummer", source = "waehlerverzeichnisNummer")
    @Mapping(target = "bezirkIDUndWaehlerverzeichnisNummer.wahlbezirkID", source = "wahlbezirkID")
    BeanstandeteWahlbriefe toEntity(BeanstandeteWahlbriefeModel model);
}
