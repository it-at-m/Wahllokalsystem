package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignis;
import java.util.List;
import lombok.val;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface EreignisModelMapper {

    // to Model
    EreignisModel toModel(final Ereignis entity);

    @Mapping(target = "ereigniseintraege", source = "ereignisse")
    EreignisseModel toEreignisseModel(final String wahlbezirkID, final boolean keineVorfaelle, final boolean keineVorkommnisse,
            final List<EreignisModel> ereignisse);

    // to Entity
    @Mapping(target = "id", ignore = true)
    Ereignis toEntity(final EreignisModel model, final String wahlbezirkID);

    default List<Ereignis> toEntity(final EreignisseWriteModel model) {
        val wahlbezirkID = model.wahlbezirkID();
        return model.ereigniseintraege().stream().map(eintrag -> toEntity(eintrag, wahlbezirkID)).toList();
    }
}
