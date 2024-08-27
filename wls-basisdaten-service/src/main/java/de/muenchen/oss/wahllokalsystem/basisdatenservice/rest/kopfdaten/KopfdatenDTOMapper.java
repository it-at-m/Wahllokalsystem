package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.kopfdaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.KopfdatenModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface KopfdatenDTOMapper {

    @Mapping(source = "bezirkUndWahlID.wahlID", target = "wahlID")
    @Mapping(source = "bezirkUndWahlID.wahlbezirkID", target = "wahlbezirkID")
    KopfdatenDTO toDTO(KopfdatenModel kopfdatenModel);

}
