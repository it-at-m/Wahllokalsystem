package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import java.util.List;

public record StimmzettelOfTeamModel(
    int stimmzettelkennung,
    boolean isValid,
    int invalideVotes,
    StimmzettelGueltigkeitModel gueltigkeit,
    List<BeschlussvormerkungModel> beschlussvormerkungen,
    BeschlussfassungModel beschlussfassung,
    List<WahlvorschlagModel> wahlvorschlaege) {}
