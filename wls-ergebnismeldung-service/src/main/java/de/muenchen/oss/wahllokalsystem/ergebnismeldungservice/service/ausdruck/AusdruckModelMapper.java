package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Ausdruck;
import java.time.Instant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AusdruckModelMapper {

    AusdruckModel toModel(Ausdruck entity);

    @Mapping(target = "erstelltAm", source = "erstelltAm")
    Ausdruck toEntity(AusdruckModel model, Instant erstelltAm);
}
