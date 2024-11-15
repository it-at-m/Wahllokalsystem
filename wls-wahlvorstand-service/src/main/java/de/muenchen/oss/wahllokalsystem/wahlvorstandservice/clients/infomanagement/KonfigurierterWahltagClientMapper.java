package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.clients.infomanagement;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.eai.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.KonfigurierterWahltagModel;
import org.mapstruct.Mapper;

@Mapper
public interface KonfigurierterWahltagClientMapper {

    KonfigurierterWahltagModel fromRemoteClientDTOToModel(KonfigurierterWahltagDTO konfigurierterWahltagDTO);
}
