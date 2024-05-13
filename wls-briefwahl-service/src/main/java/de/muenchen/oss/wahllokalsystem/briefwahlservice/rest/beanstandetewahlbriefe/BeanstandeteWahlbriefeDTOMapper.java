package de.muenchen.oss.wahllokalsystem.briefwahlservice.rest.beanstandetewahlbriefe;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.service.beanstandetewahlbriefe.BeanstandeteWahlbriefeModel;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.service.beanstandetewahlbriefe.BeanstandeteWahlbriefeReference;
import org.mapstruct.Mapper;

@Mapper
public interface BeanstandeteWahlbriefeDTOMapper {

    BeanstandeteWahlbriefeModel toCreateModel(BeanstandeteWahlbriefeCreateDTO dto, String wahlbezirkID, Long waehlerverzeichnisNummer);

    BeanstandeteWahlbriefeReference toReferenceModel(String wahlbezirkID, Long waehlerverzeichnisNummer);

    BeanstandeteWahlbriefeDTO toDTO(BeanstandeteWahlbriefeModel model);
}
