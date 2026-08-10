package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.Kandidat;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.Stimmzettel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.Wahlvorschlag;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.TeamBezirkUndWahlIDModel;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface StimmzettelModelMapper {

  @Mapping(target = "stimmzettelkennung", source = "id.stimmzettelkennung")
  @Mapping(target = "gueltigkeit", source = "gueltigkeit")
  StimmzettelOfTeamModel toModel(Stimmzettel stimmzettel);

  @Mapping(target = "wahlvorschlag", ignore = true)
  Kandidat toEntity(KandidatModel kandidatModel);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "stimmzettel", ignore = true)
  Wahlvorschlag toEntity(WahlvorschlagModel wahlvorschlagModel);

  @Mapping(target = "id.teamID", source = "owner.teamID")
  @Mapping(target = "id.wahlbezirkID", source = "owner.wahlbezirkID")
  @Mapping(target = "id.wahlID", source = "owner.wahlID")
  @Mapping(target = "id.stimmzettelkennung", source = "stimmzettelModel.stimmzettelkennung")
  @Mapping(target = "gueltigkeit", source = "stimmzettelModel.gueltigkeit")
  @Mapping(target = "beschlussvorschlag.id", ignore = true)
  @Mapping(target = "beschlussvorschlag.stimmzettel", ignore = true)
  Stimmzettel toEntity(TeamBezirkUndWahlIDModel owner, StimmzettelOfTeamModel stimmzettelModel);
}
