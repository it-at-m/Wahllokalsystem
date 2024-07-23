package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlage;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Referendumvorlage;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Referendumvorlagen;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface ReferendumvorlageModelMapper {

    BezirkUndWahlID toBezirkUndWahlID(ReferendumvorlagenReferenceModel referendumvorlagenReferenceModel);

    ReferendumvorlagenModel toModel(Referendumvorlagen entity);

    @Mapping(target = "id", ignore = true)
    Referendumvorlagen toEntity(ReferendumvorlagenModel model, BezirkUndWahlID bezirkUndWahlID);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "referendumvorlagen", ignore = true)
    Referendumvorlage toEntity(ReferendumvorlageModel model);
}
