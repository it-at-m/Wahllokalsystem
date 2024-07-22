package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.ReferendumvorlageDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.ReferendumvorlagenDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlage.ReferendumvorlageModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlage.ReferendumvorlagenModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ReferendumvorlagenClientMapper {

    @Mapping(target = "referendumvorlagen", source = "referendumvorlagen")
    ReferendumvorlagenModel toModel(ReferendumvorlagenDTO referendumvorlagenDTO);

    @Mapping(target = "referendumoptionen", source = "referendumoptionen")
    ReferendumvorlageModel toModel(ReferendumvorlageDTO referendumvorlageDTO);
}
