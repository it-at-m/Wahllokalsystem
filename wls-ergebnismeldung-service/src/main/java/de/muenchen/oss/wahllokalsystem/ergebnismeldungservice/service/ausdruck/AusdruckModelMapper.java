package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Ausdruck;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface AusdruckModelMapper {

    AusdruckModel toModel(Ausdruck entity);

    Ausdruck toEntity(AusdruckModel model);

    List<AusdruckModel> toModelList(List<Ausdruck> ausdruckEntities);
}
