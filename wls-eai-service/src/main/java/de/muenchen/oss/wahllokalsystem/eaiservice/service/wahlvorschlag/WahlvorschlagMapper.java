package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlvorschlag;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Kandidat;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Referendumoption;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Referendumvorlage;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Referendumvorlagen;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Wahlvorschlaege;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Wahlvorschlag;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.KandidatDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.ReferendumoptionDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.ReferendumvorlageDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.ReferendumvorlagenDTO;
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

    ReferendumvorlagenDTO toDTO(Referendumvorlagen referendumvorlagen);

    ReferendumvorlageDTO toDTO(Referendumvorlage referendumvorlage);

    ReferendumoptionDTO toDTO(Referendumoption referendumoption);
}
