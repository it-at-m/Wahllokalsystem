package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.briefwahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.Briefwahlvorbereitung;
import org.mapstruct.Mapper;

@Mapper
public interface BriefwahlvorbereitungModelMapper {

    BriefwahlvorbereitungModel toModel(Briefwahlvorbereitung entity);

    Briefwahlvorbereitung toEntity(BriefwahlvorbereitungModel model);
}
