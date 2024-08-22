package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignis;
import org.mapstruct.Mapper;

@Mapper
public interface EreignisModelMapper {

    EreignisModel toModel(final Ereignis entity);

    Ereignis toEntity(final EreignisModel model);
}
