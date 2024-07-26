package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.referendumvorlagen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen.ReferendumvorlagenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen.ReferendumvorlagenReferenceModel;
import org.mapstruct.Mapper;

@Mapper
public interface ReferendumvorlagenDTOMapper {

    ReferendumvorlagenDTO toDTO(ReferendumvorlagenModel referendumvorlagenModel);

    ReferendumvorlagenReferenceModel toModel(String wahlbezirkID, String wahlID);
}
