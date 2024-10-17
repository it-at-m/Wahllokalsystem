package de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl;

import de.muenchen.oss.wahllokalsystem.monitoringservice.domain.waehleranzahl.Waehleranzahl;
import org.mapstruct.Mapper;

@Mapper
public interface WaehleranzahlModelMapper {

    Waehleranzahl toEntity(WaehleranzahlModel waehleranzahlModel);

    WaehleranzahlModel toModel(Waehleranzahl entity);

}