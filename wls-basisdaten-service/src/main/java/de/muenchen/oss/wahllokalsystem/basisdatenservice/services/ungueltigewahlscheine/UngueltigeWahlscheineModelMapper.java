package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.ungueltigewahlscheine;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.UngueltigeWahlscheine;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahltagIdUndWahlbezirksart;
import org.mapstruct.Mapper;

@Mapper
public interface UngueltigeWahlscheineModelMapper {

    WahltagIdUndWahlbezirksart toID(UngueltigeWahlscheineReference ungueltigeWahlscheineReference);

    UngueltigeWahlscheine toEntity(UngueltigeWahlscheineWriteModel ungueltigeWahlscheineWriteModel);
}
