package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record StimmzettelOfTeamModel(
    @NotNull Integer stimmzettelkennung,
    @NotNull Integer invalideVotes,
    @NotNull StimmzettelGueltigkeitModel gueltigkeit,
    List<BeschlussgrundModel> beschlussvorschlag,
    BeschlussfassungModel beschlussfassung,
    List<WahlvorschlagModel> wahlvorschlaege) {}
