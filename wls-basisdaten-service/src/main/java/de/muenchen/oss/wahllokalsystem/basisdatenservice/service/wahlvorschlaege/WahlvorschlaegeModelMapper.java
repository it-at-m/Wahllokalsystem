package de.muenchen.oss.wahllokalsystem.basisdatenservice.service.wahlvorschlaege;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.aoueai.domain.Wahlvorschlaege;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WLSWahlvorschlaege;
import org.mapstruct.Mapper;

@Mapper
public interface WahlvorschlaegeModelMapper {

    WahlvorschlaegeModel toModel(WLSWahlvorschlaege entity);

    WLSWahlvorschlaege toEntity(WahlvorschlaegeModel model);

    WLSWahlvorschlaege toEntity(Wahlvorschlaege remoteModel);
}
