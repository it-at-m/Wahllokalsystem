package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Ausdruck;
import org.mapstruct.Mapper;

@Mapper
public interface AusdruckModelMapper {

    AusdruckModel toModel(Ausdruck entity);

    Ausdruck toEntity(AusdruckModel model);
}
