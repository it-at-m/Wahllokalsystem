package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahltag;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface WahltagModelMapper {

    List<WahltagModel> fromWahltagEntityToWahltagModelList(List<Wahltag> entities);

    List<Wahltag> fromWahltagModelToWahltagEntityList(List<WahltagModel> entities);
}
