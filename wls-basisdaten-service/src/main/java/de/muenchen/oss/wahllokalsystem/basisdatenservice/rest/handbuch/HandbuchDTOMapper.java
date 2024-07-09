package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.handbuch;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.handbuch.HandbuchReferenceModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.handbuch.HandbuchWriteModel;
import org.mapstruct.Mapper;

@Mapper
public interface HandbuchDTOMapper {

    HandbuchWriteModel toModel(HandbuchReferenceModel handbuchReferenceModel, byte[] handbuchData);

    HandbuchReferenceModel toModel(String wahltagID, WahlbezirkArtDTO wahlbezirksart);
}
