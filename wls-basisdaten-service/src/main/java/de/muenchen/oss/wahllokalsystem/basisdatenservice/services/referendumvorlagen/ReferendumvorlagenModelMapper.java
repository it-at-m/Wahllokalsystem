package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.Referendumvorlage;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.Referendumvorlagen;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface ReferendumvorlagenModelMapper {

    BezirkUndWahlID toBezirkUndWahlID(ReferendumvorlagenReferenceModel referendumvorlagenReferenceModel);

    ReferendumvorlagenModel toModel(Referendumvorlagen entity);

    @Mapping(target = "id", ignore = true)
    Referendumvorlagen toEntity(ReferendumvorlagenModel model, BezirkUndWahlID bezirkUndWahlID);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "referendumvorlagen", ignore = true)
    Referendumvorlage toEntity(ReferendumvorlageModel model);
}
