package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlage;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Referendumvorlage;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Referendumvorlagen;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import java.util.stream.Collectors;
import lombok.val;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface ReferendumvorlageModelMapper {

    BezirkUndWahlID toBezirkUndWahlID(ReferendumvorlagenReferenceModel referendumvorlagenReferenceModel);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "referendumvorlagen", ignore = true)
    Referendumvorlage toEntity(ReferendumvorlageModel referendumvorlageModel, String stimmzettelgebietID, BezirkUndWahlID bezirkUndWahlID);

    ReferendumvorlageModel toModel(Referendumvorlage entity);

    ReferendumvorlagenModel toModel(Referendumvorlagen entity);

    Referendumvorlagen toEntity(ReferendumvorlagenModel model, BezirkUndWahlID bezirkUndWahlID);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "referendumvorlagen", ignore = true)
    Referendumvorlage toEntity(ReferendumvorlageModel model);

    default List<Referendumvorlage> toListOfEntities(final ReferendumvorlagenModel referendumvorlagenModel, BezirkUndWahlID bezirkUndWahlID) {
        val stimmzettelgebietID = referendumvorlagenModel.stimmzettelgebietID();
        return referendumvorlagenModel.referendumvorlagen().stream().map(vorlageModel -> toEntity(vorlageModel, stimmzettelgebietID, bezirkUndWahlID)).toList();
    }

    default ReferendumvorlagenModel toModel(final List<Referendumvorlage> entities, final String stimmzettelgebietID) {
        return new ReferendumvorlagenModel(stimmzettelgebietID, entities.stream().map(this::toModel).collect(Collectors.toSet()));
    }
}
