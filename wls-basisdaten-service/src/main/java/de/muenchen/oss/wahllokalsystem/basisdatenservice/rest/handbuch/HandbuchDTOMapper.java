package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.handbuch;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.common.WahlbezirkArtDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.handbuch.HandbuchReferenceModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.handbuch.HandbuchWriteModel;
import org.mapstruct.Mapper;

@Mapper
public interface HandbuchDTOMapper {

  HandbuchWriteModel toModel(HandbuchReferenceModel handbuchReferenceModel, byte[] handbuchData);

  HandbuchReferenceModel toModel(String wahltagID, WahlbezirkArtDTO wahlbezirksart);
}
