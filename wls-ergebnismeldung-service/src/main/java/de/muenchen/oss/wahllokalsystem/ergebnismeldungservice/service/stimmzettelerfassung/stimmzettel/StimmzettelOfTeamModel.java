package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record StimmzettelOfTeamModel(
    @NotNull Integer stimmzettelkennung,
    @NotNull Integer invalideVotes,
    @NotNull StimmzettelGueltigkeitModel gueltigkeit,
    List<WahlvorstandBeschlussgrundModel> wahlvorstandBeschlussvorschlag,
    List<SystemBeschlussgrundModel> systemBeschlussvorschlag,
    BeschlussfassungModel beschlussfassung,
    List<WahlvorschlagModel> wahlvorschlaege) {}
