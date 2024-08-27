package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisse;
import org.mapstruct.Mapper;

@Mapper
public interface EreignisModelMapper {

    EreignisModel toModel(final Ereignisse entity);

    Ereignisse toEntity(final EreignisModel model);
}
