package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.ungueltigewahlscheine;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.common.WahlbezirkArtDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.ungueltigewahlscheine.UngueltigeWahlscheineReference;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.ungueltigewahlscheine.UngueltigeWahlscheineWriteModel;
import org.mapstruct.Mapper;

@Mapper
public interface UngueltigeWahlscheineDTOMapper {

    UngueltigeWahlscheineReference toModel(String wahltagID, WahlbezirkArtDTO wahlbezirksart);

    UngueltigeWahlscheineWriteModel toModel(UngueltigeWahlscheineReference ungueltigeWahlscheineReference, byte[] ungueltigeWahlscheineData);
}
