package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.ReferendumvorlageDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.ReferendumvorlagenDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlage.ReferendumvorlageModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlage.ReferendumvorlagenModel;
import org.mapstruct.Mapper;

@Mapper
public interface ReferendumvorlagenClientMapper {
    ReferendumvorlagenModel toModel(ReferendumvorlagenDTO referendumvorlagenDTO);

    ReferendumvorlageModel toModel(ReferendumvorlageDTO referendumvorlageDTO);
}
