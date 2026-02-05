package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.referendumvorlagen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.referendumvorlagen.ReferendumvorlagenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.referendumvorlagen.ReferendumvorlagenReferenceModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ReferendumvorlagenDTOMapper {

  @Mapping(target = "referendumvorlage", source = "referendumvorlagen")
  ReferendumvorlagenDTO toDTO(ReferendumvorlagenModel referendumvorlagenModel);

  ReferendumvorlagenReferenceModel toModel(String wahlbezirkID, String wahlID);
}
