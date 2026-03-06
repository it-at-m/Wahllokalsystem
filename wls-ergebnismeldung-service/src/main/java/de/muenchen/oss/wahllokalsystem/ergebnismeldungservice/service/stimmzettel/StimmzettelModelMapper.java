package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettel;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettel.Stimmzettel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettel.StimmzettelKandidat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface StimmzettelModelMapper {
  @Mapping(target = "combinedId.wahlbezirkID", source = "wahlbezirkID")
  @Mapping(target = "combinedId.wahlID", source = "wahlID")
  @Mapping(target = "combinedId.stimmzettelNummer", source = "stimmzettelNummer")
  Stimmzettel toEntity(StimmzettelModel stimmzettelModel);

  @Mapping(target = "discarded", source = "isDiscarded")
  StimmzettelKandidat toEntity(StimmzettelKandidatModel stimmzettelKandidatModel);

  @Mapping(target = "wahlbezirkID", source = "combinedId.wahlbezirkID")
  @Mapping(target = "wahlID", source = "combinedId.wahlID")
  @Mapping(target = "stimmzettelNummer", source = "combinedId.stimmzettelNummer")
  StimmzettelModel toModel(Stimmzettel stimmzettel);

  @Mapping(target = "isDiscarded", source = "discarded")
  StimmzettelKandidatModel toModel(StimmzettelKandidat stimmzettelKandidat);
}
