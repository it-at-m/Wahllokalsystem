package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.handbuch;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common.WahltagIdUndWahlbezirksart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.handbuch.Handbuch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface HandbuchModelMapper {

    WahltagIdUndWahlbezirksart toEntityID(HandbuchReferenceModel handbuchReferenceModel);

    @Mapping(target = "wahltagIdUndWahlbezirksart.wahltagID", source = "handbuchReferenceModel.wahltagID")
    @Mapping(target = "wahltagIdUndWahlbezirksart.wahlbezirksart", source = "handbuchReferenceModel.wahlbezirksart")
    @Mapping(target = "handbuch", source = "handbuchData")
    Handbuch toEntity(HandbuchWriteModel handbuchWriteModel);
}
