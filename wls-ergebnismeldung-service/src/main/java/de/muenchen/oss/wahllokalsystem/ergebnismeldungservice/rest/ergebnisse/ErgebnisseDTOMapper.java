package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnisse;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common.StapelartDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseReferenceModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ErgebnisseDTOMapper {

  @Mapping(target = "wahlbezirkID", source = "bezirkUndWahlIDStapelart.wahlbezirkID")
  @Mapping(target = "wahlID", source = "bezirkUndWahlIDStapelart.wahlID")
  @Mapping(target = "stapelart", source = "bezirkUndWahlIDStapelart.stapelart")
  ErgebnisseModel toModel(ErgebnisseDTO dto);

  @Mapping(target = "bezirkUndWahlIDStapelart.wahlbezirkID", source = "wahlbezirkID")
  @Mapping(target = "bezirkUndWahlIDStapelart.wahlID", source = "wahlID")
  @Mapping(target = "bezirkUndWahlIDStapelart.stapelart", source = "stapelart")
  ErgebnisseDTO toDTO(ErgebnisseModel entity);

  ErgebnisseReferenceModel toReferenceModel(
      String wahlbezirkID, String wahlID, StapelartDTO stapelart);
}
