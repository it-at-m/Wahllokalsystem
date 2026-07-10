package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.Stimmzettel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelKandidat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface StimmzettelModelMapper {

  @Mapping(target = "stimmzettelkennung", source = "id.stimmzettelkennung")
  StimmzettelOfTeamModel toModel(Stimmzettel stimmzettel);

  @Mapping(target = "isDiscarded", source = "discarded")
  StimmzettelKandidatModel toModel(StimmzettelKandidat stimmzettelKandidat);

  @Mapping(target = "id.teamID", source = "owner.teamID")
  @Mapping(target = "id.wahlbezirkID", source = "owner.wahlbezirkID")
  @Mapping(target = "id.wahlID", source = "owner.wahlID")
  @Mapping(target = "id.stimmzettelkennung", source = "stimmzettelModel.stimmzettelkennung")
  Stimmzettel toEntity(StimmzettelOwnerModel owner, StimmzettelOfTeamModel stimmzettelModel);

  @Mapping(target = "discarded", source = "isDiscarded")
  StimmzettelKandidat toEntity(StimmzettelKandidatModel stimmzettelKandidatModel);
}
