package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.Beschlussfassung;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.DSEKandidat;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.DSEStimmzettel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.DSEWahlvorschlag;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.KandidatID;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelID;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelGueltigkeit;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.TeamBezirkUndWahlIDModel;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface StimmzettelModelMapper {

  @Mapping(target = "stimmzettelkennung", source = "id.stimmzettelkennung")
  @Mapping(target = "gueltigkeit", source = "gueltigkeit")
  StimmzettelOfTeamModel toModel(DSEStimmzettel stimmzettel);

  BeschlussfassungModel toModel(Beschlussfassung beschlussfassung);

  Beschlussfassung toEntity(BeschlussfassungModel beschlussfassungModel);

  KandidatModel toModel(DSEKandidat kandidat);

  @Mapping(target = "wahlvorschlag", ignore = true)
  DSEKandidat toEntity(KandidatModel kandidatModel);

  KandidatIDModel toModel(KandidatID kandidatID);

  KandidatID toEntity(KandidatIDModel kandidatIDModel);

  WahlvorschlagModel toModel(DSEWahlvorschlag wahlvorschlag);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "stimmzettel", ignore = true)
  DSEWahlvorschlag toEntity(WahlvorschlagModel wahlvorschlagModel);

  StimmzettelGueltigkeitModel toModel(StimmzettelGueltigkeit gueltigkeit);

  StimmzettelGueltigkeit toEntity(StimmzettelGueltigkeitModel gueltigkeitModel);

  @Mapping(target = "id.teamID", source = "owner.teamID")
  @Mapping(target = "id.wahlbezirkID", source = "owner.wahlbezirkID")
  @Mapping(target = "id.wahlID", source = "owner.wahlID")
  @Mapping(target = "id.stimmzettelkennung", source = "stimmzettelModel.stimmzettelkennung")
  @Mapping(target = "gueltigkeit", source = "stimmzettelModel.gueltigkeit")
  @Mapping(target = "beschlussvormerkungen.id", ignore = true)
  @Mapping(target = "beschlussvormerkungen.stimmzettel", ignore = true)
  DSEStimmzettel toEntity(TeamBezirkUndWahlIDModel owner, StimmzettelOfTeamModel stimmzettelModel);

  default StimmzettelID toStimmzettelID(
      final TeamBezirkUndWahlIDModel owner, final StimmzettelOfTeamModel stimmzettelModel) {
    return new StimmzettelID(
        owner.wahlbezirkID(), owner.wahlID(), owner.teamID(), stimmzettelModel.stimmzettelkennung());
  }
}
