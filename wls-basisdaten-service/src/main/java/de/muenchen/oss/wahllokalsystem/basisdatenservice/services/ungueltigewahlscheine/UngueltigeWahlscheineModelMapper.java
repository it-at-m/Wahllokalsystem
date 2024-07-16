package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.ungueltigewahlscheine;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.UngueltigeWahlscheine;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahltagIdUndWahlbezirksart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UngueltigeWahlscheineModelMapper {

    WahltagIdUndWahlbezirksart toID(UngueltigeWahlscheineReferenceModel ungueltigeWahlscheineReferenceModel);

    @Mapping(target = "wahltagIdUndWahlbezirksart.wahltagID", source = "ungueltigeWahlscheineReference.wahltagID")
    @Mapping(target = "wahltagIdUndWahlbezirksart.wahlbezirksart", source = "ungueltigeWahlscheineReference.wahlbezirksart")
    @Mapping(target = "ungueltigeWahlscheine", source = "ungueltigeWahlscheineData")
    UngueltigeWahlscheine toEntity(UngueltigeWahlscheineWriteModel ungueltigeWahlscheineWriteModel);
}
