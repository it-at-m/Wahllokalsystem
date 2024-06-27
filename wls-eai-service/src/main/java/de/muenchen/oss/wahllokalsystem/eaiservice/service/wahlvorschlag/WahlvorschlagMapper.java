package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlvorschlag;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Kandidat;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Wahlvorschlaege;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Wahlvorschlag;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.KandidatDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.WahlvorschlaegeDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.WahlvorschlagDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WahlvorschlagMapper {

    WahlvorschlaegeDTO toDTO(Wahlvorschlaege wahlvorstand);

    @Mapping(target = "identifikator", source = "id")
    WahlvorschlagDTO toDTO(Wahlvorschlag wahlvorschlag);

    @Mapping(target = "identifikator", source = "id")
    KandidatDTO toDTO(Kandidat kandidat);
}
