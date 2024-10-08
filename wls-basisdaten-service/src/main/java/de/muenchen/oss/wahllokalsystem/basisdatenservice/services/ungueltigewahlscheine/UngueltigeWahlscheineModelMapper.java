package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.ungueltigewahlscheine;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common.WahltagIdUndWahlbezirksart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.ungueltigewahlscheine.UngueltigeWahlscheine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UngueltigeWahlscheineModelMapper {

    WahltagIdUndWahlbezirksart toID(UngueltigeWahlscheineReferenceModel ungueltigeWahlscheineReferenceModel);

    @Mapping(target = "wahltagIdUndWahlbezirksart.wahltagID", source = "ungueltigeWahlscheineReferenceModel.wahltagID")
    @Mapping(target = "wahltagIdUndWahlbezirksart.wahlbezirksart", source = "ungueltigeWahlscheineReferenceModel.wahlbezirksart")
    @Mapping(target = "ungueltigeWahlscheine", source = "ungueltigeWahlscheineData")
    UngueltigeWahlscheine toEntity(UngueltigeWahlscheineWriteModel ungueltigeWahlscheineWriteModel);
}
