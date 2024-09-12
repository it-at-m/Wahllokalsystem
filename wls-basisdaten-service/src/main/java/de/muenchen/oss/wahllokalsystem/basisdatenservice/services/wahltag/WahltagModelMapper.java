package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahltag;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface WahltagModelMapper {

    Wahltag toEntity(WahltagModel wahltagModel);

    WahltagModel toModel(Wahltag wahltag);

    List<WahltagModel> fromWahltagEntityToWahltagModelList(List<Wahltag> entities);

    List<Wahltag> fromWahltagModelToWahltagEntityList(List<WahltagModel> entities);
}
