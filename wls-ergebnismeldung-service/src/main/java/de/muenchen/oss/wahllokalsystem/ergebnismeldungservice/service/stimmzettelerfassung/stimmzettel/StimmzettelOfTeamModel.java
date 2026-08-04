package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import java.util.List;

public record StimmzettelOfTeamModel(
    int stimmzettelkennung,
    int invalideVotes,
    StimmzettelGueltigkeitModel gueltigkeit,
    List<BeschlussgrundModel> beschlussvorschlag,
    BeschlussfassungModel beschlussfassung,
    List<WahlvorschlagModel> wahlvorschlaege) {}
