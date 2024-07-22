package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.referendumvorlage;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlage.ReferendumvorlagenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlage.ReferendumvorlagenReferenceModel;
import org.mapstruct.Mapper;

@Mapper
public interface ReferendumvorlagenDTOMapper {

    ReferendumvorlagenDTO toDTO(ReferendumvorlagenModel referendumvorlagenModel);

    ReferendumvorlagenReferenceModel toModel(String wahlbezirkID, String wahlID);
}
