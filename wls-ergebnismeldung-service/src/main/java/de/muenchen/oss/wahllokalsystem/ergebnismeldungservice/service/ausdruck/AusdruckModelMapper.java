package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Ausdruck;
import java.time.Instant;
import org.mapstruct.Mapper;

@Mapper
public interface AusdruckModelMapper {

    AusdruckReadModel toModel(Ausdruck entity);

    Ausdruck toEntity(AusdruckWriteModel model, Instant erstelltAm);
}
