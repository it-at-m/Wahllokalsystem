package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.Beschlussfassung;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.DSEKandidat;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.DSEStimmzettel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.DSEWahlvorschlag;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.KandidatID;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelID;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelGueltigkeit;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.TeamBezirkUndWahlIDModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface StimmzettelModelMapper {

  @Mapping(target = "stimmzettelkennung", source = "id.stimmzettelkennung")
  @Mapping(target = "gueltigkeit", source = "gueltigkeit")
  StimmzettelOfTeamModel toModel(DSEStimmzettel stimmzettel);

  BeschlussfassungModel toModel(Beschlussfassung beschlussfassung);

  Beschlussfassung toEntity(BeschlussfassungModel beschlussfassungModel);

  KandidatModel toModel(DSEKandidat kandidat);

  DSEKandidat toEntity(KandidatModel kandidatModel);

  KandidatIDModel toModel(KandidatID kandidatID);

  KandidatID toEntity(KandidatIDModel kandidatIDModel);

  WahlvorschlagModel toModel(DSEWahlvorschlag wahlvorschlag);

  DSEWahlvorschlag toEntity(WahlvorschlagModel wahlvorschlagModel);

  StimmzettelGueltigkeitModel toModel(StimmzettelGueltigkeit gueltigkeit);

  StimmzettelGueltigkeit toEntity(StimmzettelGueltigkeitModel gueltigkeitModel);

  @Mapping(target = "id.teamID", source = "owner.teamID")
  @Mapping(target = "id.wahlbezirkID", source = "owner.wahlbezirkID")
  @Mapping(target = "id.wahlID", source = "owner.wahlID")
  @Mapping(target = "id.stimmzettelkennung", source = "stimmzettelModel.stimmzettelkennung")
  @Mapping(target = "gueltigkeit", source = "stimmzettelModel.gueltigkeit")
  DSEStimmzettel toEntity(TeamBezirkUndWahlIDModel owner, StimmzettelOfTeamModel stimmzettelModel);

  default StimmzettelID toStimmzettelID(
      final TeamBezirkUndWahlIDModel owner, final StimmzettelOfTeamModel stimmzettelModel) {
    return new StimmzettelID(
        owner.wahlbezirkID(), owner.wahlID(), owner.teamID(), stimmzettelModel.stimmzettelkennung());
  }
}
